package debug.ws.handler;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import security.ws.handler.SignatureHandler;

/**
 * This class should be removed for production.
 * It only serves itslef useful for tests
 */
public class AttackSimulationHelper {
	
	private int _state;
	private SOAPMessage _message;
	
	public AttackSimulationHelper(int state, SOAPMessage m) {
		_state = state;
		_message = m;
	}
	
	public void attack() throws Exception{
		
		switch(_state){
		    case 0 : // not test
		      break;
		    case 1 : // increase request maximum price
		    	increasePriceAttack();
		      break;
		    case 2 : // modify the signature
		    	alterSignatureAttack();
		    	break;
		    case 3 : // modify the message counter value
		    	alterCounterValueAttack();
		    	break;
		    case 4 :
		    	alterDestinationAttack();
		    	break;
		    case 5 :
		    	alterSenderAttack();
		    	break;
		}
		
	}


	private void increasePriceAttack() throws Exception{
		SOAPBody element = _message.getSOAPBody();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	    System.out.println("The old message:\n" + bodyText + "\n--------");
	    
	    Node priceTag = element.getElementsByTagName("price").item(0);
	    
	    int price = Integer.parseInt(priceTag.getTextContent());
	    System.out.println("OLD PRICE: " + price);
	    
	    priceTag.setTextContent("90");
	    _message.saveChanges();
	    
	    price = Integer.parseInt(priceTag.getTextContent());
	    System.out.println("NEW PRICE: " + price);
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    _message.writeTo(out);
	    String newMessage = new String(out.toByteArray());
	    
	    System.out.println("The new message:\n" + newMessage + "\n--------");
	}
	
	
	private void alterSignatureAttack() throws Exception{
		SOAPHeader element = _message.getSOAPHeader();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	    System.out.println("The old message:\n" + bodyText + "\n--------");
	    
	    Node signatureHeader = element.getElementsByTagName("e:" + SignatureHandler.SIGN_HEADER).item(0);
	    byte[] bytes = signatureHeader.getTextContent().getBytes();
	    bytes[0] = '*';
	    signatureHeader.setTextContent(new String(bytes));
	    _message.saveChanges();
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    _message.writeTo(out);
	    String newMessage = new String(out.toByteArray());
	    
	    System.out.println("The new message:\n" + newMessage + "\n--------");
		
	}
	
	private void alterCounterValueAttack() throws Exception{	
		SOAPHeader element = _message.getSOAPHeader();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	    System.out.println("The old message:\n" + bodyText + "\n--------");
	    
	    Node signatureHeader = element.getElementsByTagName("e:" + SignatureHandler.MSGCOUNTER_HEADER).item(0);
	    byte[] bytes = signatureHeader.getTextContent().getBytes();
	    bytes[0] = '*';
	    signatureHeader.setTextContent(new String(bytes));
	    _message.saveChanges();
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    _message.writeTo(out);
	    String newMessage = new String(out.toByteArray());
	    
	    System.out.println("The new message:\n" + newMessage + "\n--------");
	}
	
    private void alterDestinationAttack() throws Exception{
		SOAPHeader element = _message.getSOAPHeader();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	    System.out.println("The old message:\n" + bodyText + "\n--------");
	    
	    Node signatureHeader = element.getElementsByTagName("e:" + SignatureHandler.DESTINATION_HEADER).item(0);
	    byte[] bytes = signatureHeader.getTextContent().getBytes();
	    bytes[0] = '*';
	    signatureHeader.setTextContent(new String(bytes));
	    _message.saveChanges();
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    _message.writeTo(out);
	    String newMessage = new String(out.toByteArray());
	    
	    System.out.println("The new message:\n" + newMessage + "\n--------");	
    }
    
    private void alterSenderAttack() throws Exception{
		SOAPHeader element = _message.getSOAPHeader();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	    System.out.println("The old message:\n" + bodyText + "\n--------");
	    
	    Node signatureHeader = element.getElementsByTagName("e:" + SignatureHandler.SENDER_HEADER).item(0);
	    byte[] bytes = signatureHeader.getTextContent().getBytes();
	    bytes[0] = '*';
	    signatureHeader.setTextContent(new String(bytes));
	    _message.saveChanges();
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    _message.writeTo(out);
	    String newMessage = new String(out.toByteArray());
	    
	    System.out.println("The new message:\n" + newMessage + "\n--------");	
    }

}	

