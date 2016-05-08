package pt.upa.broker.ws;

import javax.xml.registry.JAXRException;

import pt.upa.broker.backup.ws.BrokerBackupEndpointManager;

public class Broker {
	
    private String _uddiURL;
    private String _name;
    private String _url;
    private boolean _isBackup;
    
    private EndpointManager _endpoint;
    
	public Broker(String uddiURL, String name, String url, boolean isBackup) {
		_uddiURL = uddiURL;
		_name = name;
		_url = url;
		_isBackup = isBackup;
		
		if (_isBackup){
			_endpoint = new BrokerBackupEndpointManager(_uddiURL, this);
		}
		else{
			_endpoint = new BrokerEndpointManager(_uddiURL);
		}
	}
	
	public void start() throws JAXRException{
      // publish endpoint
      System.out.printf("Starting %s%n", _url);
      _endpoint.start(_url);

      // publish to UDDI
      System.out.printf("Publishing '%s' to UDDI at %s%n", _name, _uddiURL);
      _endpoint.awaitConnections(_name);
	}
	
	public void stop(){
		_endpoint.stop();
	}
	
	public void makePrimary(BrokerPort port){
		if(!_isBackup) return;
		_isBackup = false;
		
		System.out.println("Making this the primary broker");
		System.out.println("Stoping backup endpoint");
		_endpoint.stop();
		
		_endpoint = new BrokerEndpointManager(_uddiURL, port);
		
		// publish endpoint
		System.out.printf("Starting %s%n", _url);
		_endpoint.start(_url);
		
		try {
			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", _name, _uddiURL);
			_endpoint.awaitConnections(_name);
		} catch (JAXRException e) {
			System.out.println("Failed publishing!");
			System.exit(1);
		}
		
	}
}
