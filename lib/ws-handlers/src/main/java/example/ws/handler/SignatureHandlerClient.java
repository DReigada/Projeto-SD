package example.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

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
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SignatureHandlerClient implements SOAPHandler<SOAPMessageContext> {

	final static String KEYSTORE_PASSWORD = "ins3cur3";
	final static String KEY_PASSWORD = "1nsecure";
	
	public static final String REQUEST_PROPERTY = "my.request.property";
	//public static final String RESPONSE_PROPERTY = "my.response.property";

	public static final String REQUEST_HEADER = "Signature";
	public static final String REQUEST_NS = "urn:UPA";

	public static final String RESPONSE_HEADER = REQUEST_HEADER;
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = SignatureHandlerClient.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			// outbound message

			// *** #2 ***
			// get token from request context
			//System.out.println("OUTBOUND RECEIVED:");
			String propertyValue = (String) smc.get(REQUEST_PROPERTY);
			System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);
			//debug
			//String propertyValue2 = (String) smc.get(RESPONSE_PROPERTY);
			//System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue2);


			// put token in request SOAP header
			try {
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// signing:
				String bodyText = sb.getTextContent().toString();
				byte[] bodyBytes = bodyText.getBytes();

				DigitalSignatureX509 sign = new DigitalSignatureX509();
				String keyStorePath = propertyValue + ".jks";
				char[] keyStorePass = KEYSTORE_PASSWORD.toCharArray();
				String kAlias = propertyValue;
				char[] kPass = KEY_PASSWORD.toCharArray();
				PrivateKey privateKey = sign.getPrivateKeyFromKeystore(keyStorePath, keyStorePass, kAlias, kPass);

				byte[] digitalSignature = sign.makeDigitalSignature(bodyBytes, privateKey);


				//System.out.println("Signature Bytes:");
				//System.out.println(printHexBinary(digitalSignature));

				// encoding binary data with base 64
				String signatureText = printBase64Binary(digitalSignature);
				//System.out.print("Signature bytes in Base64: ");
				//System.out.println(signatureText);



				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name sender = se.createName("Sender", "e", REQUEST_NS);
				SOAPHeaderElement senderElement = sh.addHeaderElement(sender);
				
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);

				// *** #3 ***
				// add header element value
				//String newValue = propertyValue + "," + TOKEN;
				//element.addTextNode(newValue);
				senderElement.addTextNode(propertyValue);
				element.addTextNode(signatureText);
				


				System.out.printf("%s put signature '%s' on request message header%n", CLASS_NAME, signatureText);

			} catch (SOAPException e) {
				System.out.printf("Failed to add SOAP header because of %s%n", e);
			} catch (Exception e) {
				System.out.printf("Failed to add Signature header because of %s%n", e);
				// TODO Auto-generated catch block
			}

		} else {
			// inbound message

			// get token from response SOAP header
			try {
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();
				SOAPBody sb = se.getBody();
				
				//System.out.println("INBOUND RECEIVED:");
				//String propertyValue = (String) smc.get(RESPONSE_PROPERTY);
				//UpaTransporterX
				//System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);

				// check body:
				String bodyText = sb.getTextContent().toString();
				byte[] bodyBytes = bodyText.getBytes();
				
				// check  header 
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				
				// get sender header element
				Name sender = se.createName("Sender", "e", REQUEST_NS);
				Iterator it = sh.getChildElements(sender);
				// check header element
				if (!it.hasNext()) {
					return true;
				}
				SOAPElement senderElement = (SOAPElement) it.next();
				System.out.println(senderElement.getTextContent());

				


				// get signature header element
				Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
				Iterator it2 = sh.getChildElements(name);
				// check header element
				if (!it2.hasNext()) {
					System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
					return true;
				}
				SOAPElement element = (SOAPElement) it2.next();

				// *** #10 ***
				// get header element value
				String headerValue = element.getValue();
				//System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);
				

				// verify signature
				//

				//System.out.print("Cipher text: ");
				//System.out.println(headerValue);
				DigitalSignatureX509 sign = new DigitalSignatureX509();

				// decoding string in base 64
				byte[] signatureBytes = parseBase64Binary(headerValue);
				//System.out.print("Ciphered bytes: ");
				//System.out.println(printHexBinary(signatureBytes));

				String certificateFile = senderElement.getTextContent() + ".cer";
				Certificate certificate = sign.readCertificateFile(certificateFile);
				PublicKey publicKey = certificate.getPublicKey();

				// verify the signature
				System.out.println("Verifying ...");

				boolean isValid = sign.verifyDigitalSignature(signatureBytes, bodyBytes, publicKey);

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
