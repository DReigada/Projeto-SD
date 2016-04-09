package pt.upa.transporter;

import java.util.List;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.TransporterPort;


public class TransporterEndpoint{
	private Transporter _transporter;
	private String _endpointURL;	
	
	private TransporterPort _port;
	private Endpoint _endpoint;
	private UDDINaming _uddiNaming;
	private String _uddiURL;
	
	private boolean _isRunning; 
	private boolean _isPublished;
	

	
	public TransporterEndpoint(Transporter transporter, String endpointURL, String uddiURL){
		_transporter = transporter;
		_endpointURL = endpointURL;
		_uddiURL = uddiURL;
		_isRunning = false;
		_isPublished = false;
	}
	
	public void startEndpoint(){
		_port = new TransporterPort();
		_endpoint = Endpoint.publish(_endpointURL, _port);
		_isPublished = true;

	}
	
	public void stopEndpoint(){
		if(!_isPublished) return;
		_endpoint.stop();
	}
	
	public void bind(){
		if(_isRunning || !_isPublished) return;
		try{
			_uddiNaming = new UDDINaming(_uddiURL);
			_uddiNaming.rebind(_transporter.getName(), _endpointURL);
			_isRunning = true;
		}
		catch(JAXRException e){
			System.err.printf("Caught exception when binding the service at: %s [%s]%n", _uddiURL, e);
		}
	}
	
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
