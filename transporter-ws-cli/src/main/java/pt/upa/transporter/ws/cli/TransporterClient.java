package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import example.ws.handler.SignatureHandler;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient implements TransporterPortType{
	
	public static final String CLASS_NAME = TransporterClient.class.getSimpleName();
	public static final String TOKEN = "UpaBroker";
	
	TransporterPortType _port;
	//int counter = 0;
	BindingProvider bindingProvider;
	Map<String, Object> requestContext;
	
	String destination;
	
	public TransporterClient(String endpointURL) {
        // get new port
        TransporterService service = new TransporterService();
        _port = service.getTransporterPort();

        bindingProvider = (BindingProvider) _port;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointURL);	   

        destination = endpointURL;
        
	}
	
	public void handle() {
        
		//counter = counter+1;
		
        // put token UpaBroker in request context
		String initialValue = TOKEN;
		System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, initialValue);
		requestContext.put(SignatureHandler.REQUEST_PROPERTY, initialValue);
      /*  
        String msgCounter = Integer.toString(counter);
        requestContext.put(SignatureHandler.COUNTER_PROPERTY, msgCounter);
        */
		System.out.println("---------------------------");
		System.out.println("Contador antes: " + SignatureHandler.counter);
		SignatureHandler.counter++;
		System.out.println("Contador depois: " + SignatureHandler.counter);
		System.out.println("---------------------------");
		
		SignatureHandler.destination = destination;
		System.out.println("---------------------------");
		System.out.println("Destino: " + SignatureHandler.destination);
		System.out.println("---------------------------");
		
	}
	
	@Override
	public String ping(String name) {
		
		handle();
		return _port.ping(name);
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		handle();
		return _port.requestJob(origin, destination, price);
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		handle();
		return _port.decideJob(id, accept);
	}

	@Override
	public JobView jobStatus(String id) {
		handle();
		return _port.jobStatus(id);
	}

	@Override
	public List<JobView> listJobs() {
		handle();
		return _port.listJobs();
	}

	@Override
	public void clearJobs() {
		handle();
		_port.clearJobs();
	}

	

	
}
