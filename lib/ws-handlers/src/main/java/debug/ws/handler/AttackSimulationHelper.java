package debug.ws.handler;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
		}
		
	}

	private void increasePriceAttack() throws Exception{
		SOAPPart sp = _message.getSOAPPart();
		SOAPEnvelope se = sp.getEnvelope();
		SOAPBody element = se.getBody();
	    DOMSource source = new DOMSource(element);
	    StringWriter stringResult = new StringWriter();
	    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
	    String bodyText = stringResult.toString();
	
	    // get the price
	    int init = bodyText.indexOf("<price>") + 7;
	    int i = init;
	    while (Character.isDigit(bodyText.charAt(i))) ++i;
	    int price = Integer.parseInt(bodyText.substring(init, i));
	
	    System.out.println("PRICE::::::::: " + price);
	
	    String newBody = bodyText.replace(price+"", "99");
	    byte[] b = newBody.getBytes(Charset.forName("UTF-8"));
	    ByteArrayInputStream byteInStream = new ByteArrayInputStream(b);

		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage newMessage = factory.createMessage(_message.getMimeHeaders(), byteInStream);
		_message.getSOAPPart().setContent(newMessage.getSOAPPart().getContent());

  }

	private void alterSignatureAttack(){}
	private void alterCounterValueAttack(){}
}	

