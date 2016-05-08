package pt.upa.broker.ws;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import javax.xml.registry.JAXRException;

public class BrokerEndpointManager implements EndpointManager{

  private String _uddiURL;
  private String _name;
  private String _url;
  private Endpoint _endpoint;
  private UDDINaming _uddiNaming;
  private BrokerPort _port;
  
  public BrokerEndpointManager(String uddiURL) {
	  this(uddiURL, new BrokerPort());
  }
  
  public BrokerEndpointManager(String uddiURL, BrokerPort brokerPort){
	    _uddiURL = uddiURL;
	    _endpoint = null;
	    _name = _url = null;
	    _uddiNaming = null;
	    
	    _port = brokerPort;
  }

  public void start(String url) {
    _url = url;
    
    _endpoint = Endpoint.create(_port);

    // publish endpoint
    _endpoint.publish(_url);
  }

  public void awaitConnections(String name) throws 
     JAXRException {
    _name = name;
    // publish to UDDI
    _uddiNaming = new UDDINaming(_uddiURL);
    _uddiNaming.rebind(_name, _url);
  }

  public void stop() {
    try {
      if (_endpoint != null) {
        // stop endpoint
        _endpoint.stop();
        System.out.printf("Stopped %s%n", _url);
      }
    } catch (Exception e) {
      System.out.printf("Caught exception when stopping: %s%n", e);
    }
    try {
      if (_uddiNaming != null) {
        // delete from UDDI
        _uddiNaming.unbind(_name);
        System.out.printf("Deleted '%s' from UDDI%n", _name);
      }
    } catch (Exception e) {
      System.out.printf("Caught exception when deleting: %s%n", e);
    }

    _endpoint = null;
    _name = _url = null;
    _uddiNaming = null;

  }
}
