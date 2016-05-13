package security.ws.handler;

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

import debug.ws.handler.AttackSimulationHelper;
import security.ws.signature.SignatureManager;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	public static int counter = 0;
	public static String destination = null;
	public static String selfB = " ";
	public static String selfT = " ";

	final static String KEYSTORE_PASSWORD = "ins3cur3";
	final static String KEY_PASSWORD = "1nsecure";
	final static String CA_CERT = "keys//ca-certificate.pem.txt";

	public static final String REQUEST_PROPERTY = "my.request.property";
	public static final String SENDER_PROPERTY = "my.sender.property";
	public static final String IS_TEST_PROPERTY = "my.isTest.property"; /* TODO: REMOVE FOR PRODUCTION */

	public static final String SIGN_HEADER = "Signature";
	public static final String REQUEST_NS = "urn:UPA";

	public static final String MSGCOUNTER_HEADER = "MsgCounter";
	public static final String SENDER_HEADER = "Sender";
	public static final String DESTINATION_HEADER = "Destination";
	public static final String SENDERCER_HEADER = "SenderCer";
	public static final String PREFIX = "e";


	public static final String CLASS_NAME = SignatureHandler.class.getSimpleName();
	
	
	private boolean _verbose = false;
	
	public SignatureHandler() {
		_verbose = System.getProperty("verbose") != null;
	}
	
	
	public boolean handleMessage(SOAPMessageContext smc) {
		return handleAll(smc);
	}
	
	public boolean handleFault(SOAPMessageContext smc) {
		return handleAll(smc);
	}
	
	public Set<QName> getHeaders() {
		return null;
	}
	
	public void close(MessageContext messageContext) {
	}

	private boolean verifyIDCounter(String destin, int counterChk) {		
		
		if (destin.equals(selfB)){
			if (counterChk == SignatureHandler.counter){
				return true;
			} else
				return false;
		} else if (destin.equals(selfT)){
			if (SignatureHandler.counter == 0 && counterChk > 0){
				return true;
				//return false;
			}
			else if (counterChk > SignatureHandler.counter) {
				return true;
				//return false;
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

	private boolean handleAll(SOAPMessageContext smc){
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		SignatureManager sigManager = new SignatureManager(KEYSTORE_PASSWORD, KEY_PASSWORD, CA_CERT);

		if (outbound) {

			// get token from request context - sender
			String origin = (String) smc.get(REQUEST_PROPERTY);
			String originURL = (String) smc.get(SENDER_PROPERTY);
			int isTest = (Integer) smc.get(IS_TEST_PROPERTY); /* TODO: REMOVE FOR PRODUCTION */

			if(_verbose)
				System.out.printf("%s received '%s'%n", CLASS_NAME, origin);
			
			// get public certificate to send to destination via SOAP header
			// ready to go

			try {
				String ownCer = sigManager.getOwnCer(origin);
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				// get body text to sign:
				String bodyText = sb.getTextContent().toString();
				String textToSign = String.valueOf(SignatureHandler.counter) +
				SignatureHandler.destination + originURL + bodyText;

				// Sign
				String signatureText = sigManager.sign(origin, textToSign);

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header elements (name, namespace prefix, namespace)
				Name msgCounter = se.createName(MSGCOUNTER_HEADER, PREFIX, REQUEST_NS);
				SOAPHeaderElement counterElement = sh.addHeaderElement(msgCounter);

				Name destination = se.createName(DESTINATION_HEADER, PREFIX, REQUEST_NS);
				SOAPHeaderElement destinationElement = sh.addHeaderElement(destination);

				Name sender = se.createName(SENDER_HEADER, PREFIX, REQUEST_NS);
				SOAPHeaderElement senderElement = sh.addHeaderElement(sender);
				
				Name senderCer = se.createName(SENDERCER_HEADER, PREFIX, REQUEST_NS);
				SOAPHeaderElement senderCerElement = sh.addHeaderElement(senderCer);

				Name name = se.createName(SIGN_HEADER, PREFIX, REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);

				counterElement.addTextNode(String.valueOf(SignatureHandler.counter));
				destinationElement.addTextNode(SignatureHandler.destination);
				senderElement.addTextNode(originURL);
				senderCerElement.addTextNode(ownCer);
				element.addTextNode(signatureText);

				AttackSimulationHelper attackHelper = new AttackSimulationHelper(isTest, msg); /* TODO: REMOVE FOR PRODUCTION */
				attackHelper.attack(); /* TODO: REMOVE FOR PRODUCTION */
				
				if(_verbose)
					System.out.printf("%s put signature '%s' on request message header%n", CLASS_NAME, signatureText);

				return true;

			} catch (SOAPException e) {
				System.out.printf("Failed to add SOAP header because of %s%n", e);
				System.exit(1);
			} catch (Exception e) {
				System.out.printf("Failed to add Signature header because of %s%n", e);
				System.exit(1);
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

				// check header 
				if (sh == null) {
					if(_verbose)
						System.out.println("Header not found.");
					return false;
				}

				SOAPElement destinationElement = getHeaderFromSOAP
						(DESTINATION_HEADER, PREFIX, REQUEST_NS, se, sh);
				SOAPElement counterElement = getHeaderFromSOAP
						(MSGCOUNTER_HEADER, PREFIX, REQUEST_NS, se, sh);	
				SOAPElement senderElement = getHeaderFromSOAP
						(SENDER_HEADER, PREFIX, REQUEST_NS, se, sh);
				SOAPElement senderCerElement = getHeaderFromSOAP
						(SENDERCER_HEADER, PREFIX, REQUEST_NS, se, sh);
				SOAPElement signatureElement = getHeaderFromSOAP
						(SIGN_HEADER, PREFIX, REQUEST_NS, se, sh);

				
				// check counter and destination validity
				if (verifyIDCounter(destinationElement.getTextContent(), Integer.parseInt(counterElement.getTextContent()))) {
					if(_verbose){
						System.out.println("-----------------------");
						System.out.println("Message valid.");
					}
					SignatureHandler.counter = Integer.parseInt(counterElement.getTextContent());
				} else {
					if(_verbose){
						System.out.println("-----------------------");
						System.out.println("Message NOT valid.");
					}
					return false;
				}

				
				// get header element value
				String headerValue = signatureElement.getValue();
				//create string to compare
				String textToVerify = String.valueOf(SignatureHandler.counter) + 

				destinationElement.getTextContent() + senderElement.getTextContent() + bodyText;
				
				// verify signature
				String certificateToDecode = senderCerElement.getTextContent();
				Certificate pubCert = sigManager.decodeCer(certificateToDecode);
				if(!sigManager.verifyAlt(pubCert, headerValue, textToVerify)){
					return false;
				}

				// put sender in destination variable.
				// in next outbound call, this value will be used to send the message to the right place
				SignatureHandler.destination = senderElement.getTextContent();
				return true;
				
			} catch (Exception e){
				return false;
			}

		}
		return false;
	}

}
