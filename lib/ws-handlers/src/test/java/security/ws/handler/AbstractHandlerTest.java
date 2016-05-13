package security.ws.handler;

import java.io.ByteArrayInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import security.ws.signature.SignatureManager;


/**
 *  Abstract handler test suite
 */
public abstract class AbstractHandlerTest {

    // static members

    protected static final String BODY_TEXT = "friend";

    /** hello-ws SOAP request message captured with LoggingHandler */
    protected static final String HELLO_SOAP_REQUEST = "<S:Envelope " +
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    "<SOAP-ENV:Header/>" +
    "<S:Body>" +
    "<ns2:sayHello xmlns:ns2=\"http://ws.example/\">" +
    "<arg0>" + BODY_TEXT + "</arg0>" +
    "</ns2:sayHello>" +
    "</S:Body></S:Envelope>";

    /** hello-ws SOAP response message captured with LoggingHandler */
    protected static final String HELLO_SOAP_RESPONSE = "<S:Envelope " +
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    "<SOAP-ENV:Header/>" +
    "<S:Body>" +
    "<ns2:sayHelloResponse xmlns:ns2=\"http://ws.example/\">" +
    "<return>Hello friend!</return>" +
    "</ns2:sayHelloResponse>" +
    "</S:Body></S:Envelope>";

    /** SOAP message factory */
    protected static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch(SOAPException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected final static String KEYSTORE_PASSWORD = "ins3cur3";
    protected final static String KEY_PASSWORD = "1nsecure";
    protected final static String CA_CERT = "keys//ca-certificate.pem.txt";

    protected static final String RANDOM_BROKER = "Brokerxyz";
    protected static final String RANDOM_TRANSPORTER_COMPANY = "Transporterxyz";
    protected static final int RANDOM_COUNTER = 33231;

    protected static final String REQUEST_PROPERTY = "my.request.property";
    protected static final String SENDER_PROPERTY = "my.sender.property";
    protected static final String IS_TEST_PROPERTY = "my.isTest.property"; /* TODO: REMOVE FOR PRODUCTION */
    
    public static final String REQUEST_NS = "urn:UPA";
    public static final String MSGCOUNTER_HEADER = "MsgCounter";
	public static final String SENDER_HEADER = "Sender";
	public static final String DESTINATION_HEADER = "Destination";
    public static final String SIGN_HEADER = "Signature";
    public static final String SENDERCER_HEADER = "SenderCer";
	public static final String PREFIX = "e";

    // helper functions

    protected static SOAPMessage byteArrayToSOAPMessage(byte[] msg) throws Exception {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg);
        StreamSource source = new StreamSource(byteInStream);
        SOAPMessage newMsg = MESSAGE_FACTORY.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(source);
        return newMsg;
    }

    protected static SOAPMessage signAndAddHeadersToSoapMessage(SOAPMessage m) throws Exception {
        SignatureManager sigManager = new SignatureManager(KEYSTORE_PASSWORD, KEY_PASSWORD, CA_CERT);

        // get SOAP envelope
        SOAPEnvelope se = m.getSOAPPart()
                           .getEnvelope();

        // get body text to sign:
        String bodyText = se.getBody().getTextContent().toString();
        String textToSign = RANDOM_COUNTER+"" +
            RANDOM_TRANSPORTER_COMPANY + RANDOM_BROKER + bodyText;

        // certificate
        String ownCer = sigManager.getOwnCer(RANDOM_BROKER);
        
        // Sign
        String signatureText = sigManager.sign(RANDOM_BROKER, textToSign);

        // add header elements (name, namespace prefix, namespace)
        SOAPHeader sh = se.getHeader();
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

        counterElement.addTextNode(RANDOM_COUNTER+"");
        destinationElement.addTextNode(RANDOM_TRANSPORTER_COMPANY);
        senderElement.addTextNode(RANDOM_BROKER);
        senderCerElement.addTextNode(ownCer);
        element.addTextNode(signatureText);

        return m;
    }


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

}
