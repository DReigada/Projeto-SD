package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient implements TransporterPortType{
	
	TransporterPortType _port;
	
	public TransporterClient(String endpointURL) {
        // get new port
        TransporterService service = new TransporterService();
        _port = service.getTransporterPort();

        BindingProvider bindingProvider = (BindingProvider) _port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointURL);
	}
	
	@Override
	public String ping(String name) {
		return _port.ping(name);
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
