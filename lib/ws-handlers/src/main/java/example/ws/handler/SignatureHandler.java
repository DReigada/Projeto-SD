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
	public static String selfB = " ";
	public static String selfT = " ";

	final static String KEYSTORE_PASSWORD = "ins3cur3";
	final static String KEY_PASSWORD = "1nsecure";
	final static String CA_CERT = "keys//ca-certificate.pem.txt";

	public static final String REQUEST_PROPERTY = "my.request.property";

	public static final String SIGN_HEADER = "Signature";
	public static final String REQUEST_NS = "urn:UPA";

	public static final String MSGCOUNTER_HEADER = "MsgCounter";
	public static final String SENDER_HEADER = "Sender";
	public static final String DESTINATION_HEADER = "Destination";
	public static final String PREFIX = "e";


	public static final String CLASS_NAME = SignatureHandlerClient.class.getSimpleName();

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			// get token from request context - sender
			String origin = (String) smc.get(REQUEST_PROPERTY);
			System.out.printf("%s received '%s'%n", CLASS_NAME, origin);

			try {
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// get body text to sign:
				String bodyText = sb.getTextContent().toString();
				String textToSign = String.valueOf(SignatureHandler.counter) +
						SignatureHandler.destination + origin + bodyText;

				// Sign!
				String signatureText = sign(origin, textToSign);

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header elements (name, namespace prefix, namespace)
				Name msgCounter = se.createName(MSGCOUNTER_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement counterElement = sh.addHeaderElement(msgCounter);

				Name destination = se.createName(DESTINATION_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement destinationElement = sh.addHeaderElement(destination);

				Name sender = se.createName(SENDER_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement senderElement = sh.addHeaderElement(sender);

				Name name = se.createName(SIGN_HEADER, "e", REQUEST_NS);
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

				SOAPElement destinationElement = getHeaderFromSOAP
						(DESTINATION_HEADER, PREFIX, REQUEST_NS, se, sh);
				SOAPElement counterElement = getHeaderFromSOAP
						(MSGCOUNTER_HEADER, PREFIX, REQUEST_NS, se, sh);	
				SOAPElement senderElement = getHeaderFromSOAP
						(SENDER_HEADER, PREFIX, REQUEST_NS, se, sh);
				SOAPElement signatureElement = getHeaderFromSOAP
						(SIGN_HEADER, PREFIX, REQUEST_NS, se, sh);

				SignatureHandler.counter = Integer.parseInt(counterElement.getTextContent());

				/*
				// check counter validity
				if (verifyIDCounter(destinationElement.getTextContent(), Integer.parseInt(counterElement.getTextContent()))) {
					SignatureHandler.counter = Integer.parseInt(counterElement.getTextContent());
				} else {
					System.out.println("Message not valid.");
				}
				*/
				// get header element value
				String headerValue = signatureElement.getValue();
				//create string to compare
				String textToVerify = String.valueOf(SignatureHandler.counter) + 
						destinationElement.getTextContent() + senderElement.getTextContent() + bodyText;
				// verify signature
				verify(senderElement.getTextContent(), headerValue, textToVerify);


			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			} catch (Exception e) {
				System.out.printf("Exception caught", e);

			}

		}
		return true;
	}

	private boolean verifyIDCounter(String destin, int counterChk) {
		if (destin == selfB){
			if (counterChk == SignatureHandler.counter){
				return true;
			} else
				return false;
		} else if (destin == selfT){
			if (SignatureHandler.counter == 0 && counterChk > 0){
				return true;
			}
			else if (counterChk > SignatureHandler.counter) {
				return true;
			}	
		}

		return false;

	}

	public SOAPElement getHeaderFromSOAP
	(String name, String prefix, String namespace, SOAPEnvelope soapE, SOAPHeader soapH) throws SOAPException {
		SOAPEnvelope _se = soapE;
		SOAPHeader _sh = soapH;
		Name destination = _se.createName(name, prefix, namespace);
		@SuppressWarnings("rawtypes")
		Iterator it = _sh.getChildElements(destination);
		// check header element
		if (!it.hasNext()) {
			return null;
		}
		SOAPElement element = (SOAPElement) it.next();
		return element;
	}

	public String sign (String orig, String text2Sign) throws Exception{
		String _origin = orig;
		String _text2Sign = text2Sign;
		// get signature stuff
		DigitalSignatureX509 Sign = new DigitalSignatureX509();
		String keyStorePath = "keys//" + _origin + ".jks";
		char[] keyStorePass = KEYSTORE_PASSWORD.toCharArray();
		String kAlias = _origin;
		char[] kPass = KEY_PASSWORD.toCharArray();
		PrivateKey privateKey = Sign.getPrivateKeyFromKeystore(keyStorePath, keyStorePass, kAlias, kPass);

		// sign with counter, destination, sender and bodytext

		byte[] bytesToSign = _text2Sign.getBytes();
		byte[] digitalSignature = Sign.makeDigitalSignature(bytesToSign, privateKey);
		String _signatureText = printBase64Binary(digitalSignature);
		return _signatureText;
	}

	public boolean verify (String send, String signatureValue, String text2verify) throws Exception	{

		// verify signature
		DigitalSignatureX509 Sign = new DigitalSignatureX509();
		byte[] signatureBytes = parseBase64Binary(signatureValue);

		String certificateFile = "keys//" + send + ".cer";
		
		X509CertificateCheck caCheck = new X509CertificateCheck();
		// get the sender's public certificate file
		Certificate certificate =Sign.readCertificateFile(certificateFile);
		// get the CA's certificate and public key
		Certificate caCertificate = caCheck.readCertificateFile(CA_CERT);
		PublicKey caPublicKey = caCertificate.getPublicKey();

		// check if the certificate is valid (signed by CA)
		if (caCheck.verifySignedCertificate(certificate,caPublicKey)) {
			System.out.println("The signed certificate is valid");
		} else {
			System.err.println("The signed certificate is not valid");
			return false;
		}				


		PublicKey publicKey = certificate.getPublicKey();

		// verify the signature
		System.out.println("Verifying ...");
		byte[] bytesToVerify = text2verify.getBytes();

		boolean isValid = Sign.verifyDigitalSignature(signatureBytes, bytesToVerify, publicKey);

		if (isValid) {
			System.out.println("The digital signature is valid");
			return true;
		} else {
			System.out.println("The digital signature is NOT valid");
			return false;
		}

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
