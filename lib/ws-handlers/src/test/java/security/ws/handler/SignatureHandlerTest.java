package security.ws.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;


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
        // System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;
        
        SignatureHandler.counter = RANDOM_COUNTER;
        SignatureHandler.destination = RANDOM_TRANSPORTER_COMPANY;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;
            
            smc.get(REQUEST_PROPERTY);
            result = RANDOM_BROKER;
            
            smc.getMessage();
            result = soapMessage;
            
        }};

        // Unit under test is exercised.
        SignatureHandler handler = new SignatureHandler();
        boolean handleResult = handler.handleMessage(smc);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);
        
        // TODO: remove print
        System.out.println("Message: ");
        soapMessage.writeTo(System.out);
        System.out.println(); // just to add a newline to output
        
        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        /* assert header element */
        // counter element
        Name name = soapEnvelope.createName(MSGCOUNTER_HEADER, PREFIX, REQUEST_NS);
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        // counter value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        assertEquals(RANDOM_COUNTER + "", valueString);
        
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
        
    }

    /*@Test
    public void testHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = HELLO_SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header>" +
            "<d:myHeader xmlns:d=\"http://demo\">22</d:myHeader>" +
            "</SOAP-ENV:Header>");
        //System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;

            soapMessageContext.put(HeaderHandler.CONTEXT_PROPERTY, 22);
            soapMessageContext.setScope(HeaderHandler.CONTEXT_PROPERTY, Scope.APPLICATION);
        }};

        // Unit under test is exercised.
        HeaderHandler handler = new HeaderHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);

        //soapMessage.writeTo(System.out);
    }*/

}
