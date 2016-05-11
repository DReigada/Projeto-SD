package example.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	public static int counter = 0;
	public static String destination = null;
	
	final static String KEYSTORE_PASSWORD = "ins3cur3";
	final static String KEY_PASSWORD = "1nsecure";
	final static String CA_CERT = "keys//ca-certificate.pem.txt";
	
	public static final String REQUEST_PROPERTY = "my.request.property";

	public static final String REQUEST_HEADER = "Signature";
	public static final String REQUEST_NS = "urn:UPA";

	public static final String RESPONSE_HEADER = REQUEST_HEADER;
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = SignatureHandlerClient.class.getSimpleName();
	
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			// outbound message

			// get token from request context - sender
			String origin = (String) smc.get(REQUEST_PROPERTY);
			System.out.printf("%s received '%s'%n", CLASS_NAME, origin);

			// put token in request SOAP header
			try {
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// get body text to sign:
				String bodyText = sb.getTextContent().toString();
				
				// get signature stuff
				DigitalSignatureX509 sign = new DigitalSignatureX509();
				String keyStorePath = "keys//" + origin + ".jks";
				char[] keyStorePass = KEYSTORE_PASSWORD.toCharArray();
				String kAlias = origin;
				char[] kPass = KEY_PASSWORD.toCharArray();
				PrivateKey privateKey = sign.getPrivateKeyFromKeystore(keyStorePath, keyStorePass, kAlias, kPass);

				String textToSign = String.valueOf(SignatureHandler.counter) +
									SignatureHandler.destination + origin + bodyText;
				byte[] bytesToSign = textToSign.getBytes();

				byte[] digitalSignature = sign.makeDigitalSignature(bytesToSign, privateKey);

				// encoding binary data with base 64
				String signatureText = printBase64Binary(digitalSignature);



				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header elements (name, namespace prefix, namespace)
				Name msgCounter = se.createName("MsgCounter", "e", REQUEST_NS);
				SOAPHeaderElement counterElement = sh.addHeaderElement(msgCounter);
				
				Name destination = se.createName("Destination", "e", REQUEST_NS);
				SOAPHeaderElement destinationElement = sh.addHeaderElement(destination);
				
				Name sender = se.createName("Sender", "e", REQUEST_NS);
				SOAPHeaderElement senderElement = sh.addHeaderElement(sender);
				
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);


				counterElement.addTextNode(String.valueOf(SignatureHandler.counter));
				destinationElement.addTextNode(SignatureHandler.destination);
				senderElement.addTextNode(origin);
				element.addTextNode(signatureText);

				System.out.printf("%s put signature '%s' on request message header%n", CLASS_NAME, signatureText);

			} catch (SOAPException e) {
				System.out.printf("Failed to add SOAP header because of %s%n", e);
			} catch (Exception e) {
				System.out.printf("Failed to add Signature header because of %s%n", e);
				// Do something else
			}

		} else {
			// inbound message

			try {
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();
				SOAPBody sb = se.getBody();

				// check body:
				String bodyText = sb.getTextContent().toString();
				
				// check  header 
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				// get msgcounter header element
				Name destination = se.createName("Destination", "e", REQUEST_NS);
				@SuppressWarnings("rawtypes")
				Iterator it4 = sh.getChildElements(destination);
				// check header element
				if (!it4.hasNext()) {
					return true;
				}
				SOAPElement destinationElement = (SOAPElement) it4.next();
				System.out.println(destinationElement.getTextContent());
				
				// get msgcounter header element
				Name counter = se.createName("MsgCounter", "e", REQUEST_NS);
				@SuppressWarnings("rawtypes")
				Iterator it3 = sh.getChildElements(counter);
				// check header element
				if (!it3.hasNext()) {
					return true;
				}
				SOAPElement counterElement = (SOAPElement) it3.next();
				SignatureHandler.counter = Integer.parseInt(counterElement.getTextContent());
				System.out.println(counterElement.getTextContent());
				
				// get sender header element
				Name sender = se.createName("Sender", "e", REQUEST_NS);
				@SuppressWarnings("rawtypes")
				Iterator it = sh.getChildElements(sender);
				// check header element
				if (!it.hasNext()) {
					return true;
				}
				SOAPElement senderElement = (SOAPElement) it.next();
				System.out.println(senderElement.getTextContent());

				//create string to compare
				String textToVerify = String.valueOf(SignatureHandler.counter) + 
						destinationElement.getTextContent() + senderElement.getTextContent() + bodyText;
				byte[] bytesToVerify = textToVerify.getBytes();
				
				
				// get signature header element
				Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
				@SuppressWarnings("rawtypes")
				Iterator it2 = sh.getChildElements(name);
				// check header element
				if (!it2.hasNext()) {
					System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
					return true;
				}
				SOAPElement element = (SOAPElement) it2.next();

				// get header element value
				String headerValue = element.getValue();
				//System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);
				

				// verify signature
				DigitalSignatureX509 sign = new DigitalSignatureX509();

				// decoding string in base 64
				byte[] signatureBytes = parseBase64Binary(headerValue);

				String certificateFile = "keys//" + senderElement.getTextContent() + ".cer";
				
				X509CertificateCheck caCheck = new X509CertificateCheck();
				// get the sender's public certificate file
				Certificate certificate = sign.readCertificateFile(certificateFile);
				// get the CA's certificate and public key
				Certificate caCertificate = caCheck.readCertificateFile(CA_CERT);
				PublicKey caPublicKey = caCertificate.getPublicKey();
				
				// check if the certificate is valid (signed by CA)
				if (caCheck.verifySignedCertificate(certificate,caPublicKey)) {
					System.out.println("The signed certificate is valid");
				} else {
					System.err.println("The signed certificate is not valid");
					//Do something else // exception
				}				
			
				
				PublicKey publicKey = certificate.getPublicKey();

				// verify the signature
				System.out.println("Verifying ...");

				boolean isValid = sign.verifyDigitalSignature(signatureBytes, bytesToVerify, publicKey);

				if (isValid) {
					System.out.println("The digital signature is valid");

				} else {
					System.out.println("The digital signature is NOT valid");
				}

			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			} catch (Exception e) {
				System.out.printf("Exception caught", e);
			}

		}

		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext messageContext) {
	}

}
