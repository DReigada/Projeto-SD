package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import example.ws.handler.RelayClientHandler;
import example.ws.handler.SignatureHandler;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient implements TransporterPortType{
	
	public static final String CLASS_NAME = TransporterClient.class.getSimpleName();
	public static final String TOKEN = "broker";
	
	TransporterPortType _port;
	
	BindingProvider bindingProvider;
	Map<String, Object> requestContext;
	
	public TransporterClient(String endpointURL) {
        // get new port
        TransporterService service = new TransporterService();
        _port = service.getTransporterPort();

        bindingProvider = (BindingProvider) _port;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointURL);	

	}
	
	
	@Override
	public String ping(String name) {
        //1
        // put token in request context
		String initialValue = TOKEN;
		System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, initialValue);
		requestContext.put(SignatureHandler.REQUEST_PROPERTY, initialValue);
		
		 String pingR = _port.ping(name);
		// access response context
		Map<String, Object> responseContext = bindingProvider.getResponseContext();

		// *** #12 ***
		// get token from response context
		String finalValue = (String) responseContext.get(RelayClientHandler.RESPONSE_PROPERTY);
		System.out.printf("%s got token '%s' from response context%n", CLASS_NAME, finalValue);
	
		return pingR;
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		return _port.requestJob(origin, destination, price);
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		return _port.decideJob(id, accept);
	}

	@Override
	public JobView jobStatus(String id) {
		return _port.jobStatus(id);
	}

	@Override
	public List<JobView> listJobs() {
		return _port.listJobs();
	}

	@Override
	public void clearJobs() {
		_port.clearJobs();
	}

	

	
}
