package example.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String CONTEXT_PROPERTY = "my.property";
	
    public static final String REQUEST_PROPERTY = "my.request.property";
	public static final String CLASS_NAME = SignatureHandler.class.getSimpleName();
	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:example";
	public static final String TOKEN = "broker-handler";
	
	public static final String RESPONSE_PROPERTY = "my.response.property";
	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;


    public boolean handleMessage(SOAPMessageContext smc) {
    	// *** #2 ***
    	// get token from request context
    	String propertyValue = (String) smc.get(REQUEST_PROPERTY);
    	System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);
    	
    	
        System.out.println("AddHeaderHandler: Handling message.");

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                System.out.println("Writing header in outbound SOAP message...");

                // get SOAP envelope
                SOAPMessage soapMsg = smc.getMessage();
                SOAPPart soapPart = soapMsg.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

                // add header
                SOAPHeader soapHeader = soapEnvelope.getHeader();
                if (soapHeader == null){
                    soapHeader = soapEnvelope.addHeader();
                	}
                {
                	Name name = soapEnvelope.createName(REQUEST_HEADER, "e", REQUEST_NS);
                	SOAPHeaderElement element = soapHeader.addHeaderElement(name); 
                	// *** #3 ***
    				// add header element value
    				String newValue = propertyValue + "," + TOKEN;
    				element.addTextNode(newValue);

    				System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, newValue);
                	
                }
                //System.out.println(element.getTextContent().toString());
                SOAPHeader sh2 = soapEnvelope.getHeader();
                System.out.println("sh2" + sh2.toString());
                
                
                
            } else {
                System.out.println("Reading header in inbound SOAP message...");

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // get first header element
                Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();
                int value = Integer.parseInt(valueString);

                // print received header
                System.out.println("Header value is " + value);

                // put header in a property context
                smc.put(CONTEXT_PROPERTY, value);
                // set property scope to application client/server class can access it
                smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}