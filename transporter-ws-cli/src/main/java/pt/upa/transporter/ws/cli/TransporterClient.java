package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import com.sun.xml.ws.streaming.XMLStreamReaderException;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;
import security.ws.handler.SignatureHandler;

public class TransporterClient implements TransporterPortType{
	
	public static final String CLASS_NAME = TransporterClient.class.getSimpleName();
	public static final String TOKEN = "UpaBroker";
	
	TransporterPortType _port;
	BindingProvider bindingProvider;
	Map<String, Object> requestContext;
	
	private String destination;
	private CounterBackup _counterBackup;

	private int _isTest; /* TODO: REMOVE FOR PRODUCTION */
	
	public TransporterClient(String endpointURL, CounterBackup counterBackup) {
        // get new port
        TransporterService service = new TransporterService();
        _port = service.getTransporterPort();

        bindingProvider = (BindingProvider) _port;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointURL);	   

        destination = endpointURL;
        _counterBackup = counterBackup;

        _isTest = 0; /* TODO: REMOVE FOR PRODUCTION */
	}
	
	public void setIsTest(int v) {_isTest=v;} /* TODO: REMOVE FOR PRODUCTION */

	public void handle() {
        		
        // put token UpaBroker in request context
		String initialValue = TOKEN;
		System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, initialValue);
		requestContext.put(SignatureHandler.REQUEST_PROPERTY, initialValue);
		requestContext.put(SignatureHandler.SENDER_PROPERTY, SignatureHandler.selfB);
		requestContext.put(SignatureHandler.IS_TEST_PROPERTY, _isTest); /* TODO: REMOVE FOR PRODUCTION */

		SignatureHandler.counter++;
		_counterBackup.updateMessageCounter(SignatureHandler.counter); //update the counter on the backup

		SignatureHandler.destination = destination;
	}
	
	@Override
	public String ping(String name) {
		
		handle();
		try{
			return _port.ping(name);
		} catch(XMLStreamReaderException e){
			return null;
		}
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		handle();
		try{
			return _port.requestJob(origin, destination, price);
		} catch(XMLStreamReaderException e){
			return null;
		}
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		handle();
		return _port.decideJob(id, accept);
	}

	@Override
	public JobView jobStatus(String id) {
		handle();
		try{
			return _port.jobStatus(id);
		} catch(XMLStreamReaderException e){
			return null;
		}
	}

	@Override
	public List<JobView> listJobs() {
		handle();
		try{
			return _port.listJobs();
		} catch(XMLStreamReaderException e){
			return null;
		}
	}

	@Override
	public void clearJobs() {
		handle();
		try{
			_port.clearJobs();
		} catch(XMLStreamReaderException e){
			return;
		}
	}
	
}
