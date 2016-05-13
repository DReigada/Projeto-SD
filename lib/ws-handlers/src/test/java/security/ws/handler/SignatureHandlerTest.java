package security.ws.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;
import security.ws.signature.SignatureManager;


/**
 *  Handler test suite
 */
public class SignatureHandlerTest extends AbstractHandlerTest {

    // tests

    @Test
    public void testSignatureHandlerOutbound(
        @Mocked final SOAPMessageContext smc)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        SignatureHandler.counter = RANDOM_COUNTER;
        SignatureHandler.destination = RANDOM_TRANSPORTER_COMPANY;

        SignatureManager sigManager = new SignatureManager(KEYSTORE_PASSWORD, KEY_PASSWORD, CA_CERT);

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;
            
            smc.get(REQUEST_PROPERTY);
            result = RANDOM_BROKER;

            smc.get(SENDER_PROPERTY);
            result = RANDOM_BROKER;

            smc.get(IS_TEST_PROPERTY);
            result = 0;
            
            smc.getMessage();
            result = soapMessage;
            
        }};

        // Unit under test is exercised.
        SignatureHandler handler = new SignatureHandler();
        boolean handleResult = handler.handleMessage(smc);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);
        
        // assert Signature
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        /* assert Signature element */
        // counter element
        Name name = soapEnvelope.createName(MSGCOUNTER_HEADER, PREFIX, REQUEST_NS);
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        // counter value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        assertEquals(RANDOM_COUNTER+"", valueString);
        
        // destination element
        name = soapEnvelope.createName(DESTINATION_HEADER, PREFIX, REQUEST_NS);
        it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        // destination value
        element = (SOAPElement) it.next();
        valueString = element.getValue();
        assertEquals(RANDOM_TRANSPORTER_COMPANY, valueString);
        
        // sender element
        name = soapEnvelope.createName(SENDER_HEADER, PREFIX, REQUEST_NS);
        it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        // sender value
        element = (SOAPElement) it.next();
        valueString = element.getValue();
        assertEquals(RANDOM_BROKER, valueString);

        // certificate element
        name = soapEnvelope.createName(SENDERCER_HEADER, PREFIX, REQUEST_NS);
        it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        // certificate value
        String ownCer = sigManager.getOwnCer(RANDOM_BROKER);
        element = (SOAPElement) it.next();
        valueString = element.getValue();
        assertEquals(ownCer, valueString);

        // signature element (only checks if signature is present)
        name = soapEnvelope.createName(SIGN_HEADER, PREFIX, REQUEST_NS);
        it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());

        /* assert if body is unchanged */
        SOAPBody sbody = soapEnvelope.getBody();
        assertNotNull(sbody);
        String bodyText = sbody.getTextContent().toString();
        assertEquals(BODY_TEXT, bodyText);
    }

    @Test
    public void testCorrectSignatureHeaderInSignatureHandlerOutbound(
        @Mocked final SOAPMessageContext smc)
        throws Exception {
        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        SignatureHandler.counter = RANDOM_COUNTER;
        SignatureHandler.destination = RANDOM_TRANSPORTER_COMPANY;

        SignatureManager sigManager = new SignatureManager(KEYSTORE_PASSWORD, KEY_PASSWORD, CA_CERT);

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;
            
            smc.get(REQUEST_PROPERTY);
            result = RANDOM_BROKER;

            smc.get(SENDER_PROPERTY);
            result = RANDOM_BROKER;

            smc.get(IS_TEST_PROPERTY);
            result = 0;
            
            smc.getMessage();
            result = soapMessage;
            
        }};

        // Unit under test is exercised.
        SignatureHandler handler = new SignatureHandler();
        boolean handleResult = handler.handleMessage(smc);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);
        
        /* Assert Signature */
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();

        // get signature element
        Name name = soapEnvelope.createName(SIGN_HEADER, PREFIX, REQUEST_NS);
        SOAPElement ele = (SOAPElement) soapHeader.getChildElements(name).next();
        String signature = ele.getValue();

        //create string to compare
        String textToVerify = RANDOM_COUNTER+"" + RANDOM_TRANSPORTER_COMPANY +
                RANDOM_BROKER + BODY_TEXT;
        // finally verify signature
        assertTrue(sigManager.verify(RANDOM_BROKER, signature, textToVerify));

    }

    @Test
    public void testSignatureHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST;
        SOAPMessage m = byteArrayToSOAPMessage(soapText.getBytes());
        final SOAPMessage soapMessage = signAndAddHeadersToSoapMessage(m);
        final Boolean soapOutbound = false;

        // TODO: remove print
        System.out.println("IMPORTANT | Message: ");
        soapMessage.writeTo(System.out);
        System.out.println(); // just to add a newline to output

        // set things so the VerifyIDCounter returns true
        SignatureHandler.counter = 0;
        SignatureHandler.selfT = RANDOM_TRANSPORTER_COMPANY;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};

        // Unit under test is exercised.
        SignatureHandler handler = new SignatureHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);

    }

}