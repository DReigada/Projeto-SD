package pt.upa.transporter.ws;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import example.ws.handler.SignatureHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.core.Transporter;

/**
 * This class implements an easy way of starting and binding a transporter's endpoint
 *
 */
public class TransporterEndpoint{
	
	// The transporter
	private Transporter _transporter; 
	// The endpoint URL
	private String _endpointURL;	
	// The UDDI URL
	private String _uddiURL;
	
	private TransporterPort _port;
	private Endpoint _endpoint;
	private UDDINaming _uddiNaming;

	// states of the endpoint
	private boolean _isRunning; 
	private boolean _isPublished;
	

	/**
	 * Creates an Endpoint for the given transporter
	 * @param transporter the transporter to create the endpoint
	 * @param endpointURL the endpoint URL
	 * @param uddiURL the UDDI URL
	 */
	public TransporterEndpoint(Transporter transporter, String endpointURL, String uddiURL){
		_transporter = transporter;
		_endpointURL = endpointURL;
		_uddiURL = uddiURL;
		_isRunning = false;
		_isPublished = false;
	}
	
	/**
	 * Starts the Endpoint
	 */
	public void startEndpoint(){
		_port = new TransporterPort(_transporter);
		_endpoint = Endpoint.publish(_endpointURL, _port);
		_isPublished = true;

	}
	
	/**
	 * Stops the Endpoint
	 */
	public void stopEndpoint(){
		if(!_isPublished) return;
		_endpoint.stop();
		_port.stopSimulator();
	}
	
	/**
	 * Binds the endpoint to the UDDI
	 */
	public void bind(){
		if(_isRunning || !_isPublished) return;
		try{
			_uddiNaming = new UDDINaming(_uddiURL);
			_uddiNaming.rebind(_transporter.getName(), _endpointURL);
			_isRunning = true;
			SignatureHandler.selfT = _endpointURL;
		}
		catch(JAXRException e){
			System.err.printf("Caught exception when binding the service at: %s [%s]%n", _uddiURL, e);
		}
	}
	
	/**
	 * Unbinds the endpoint from the UDDI
	 */
	public void unbind(){
		if (!_isRunning) return;
		try{
			_uddiNaming.unbind(_transporter.getName());
		}
		catch(JAXRException e){
			System.err.printf("Caught exception when unbinding service at: %s [%s]%n", _uddiURL, e);
		}
	}
	
}
