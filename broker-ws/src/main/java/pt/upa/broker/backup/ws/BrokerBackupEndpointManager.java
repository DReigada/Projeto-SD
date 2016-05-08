package pt.upa.broker.backup.ws;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.Broker;
import pt.upa.broker.ws.EndpointManager;

public class BrokerBackupEndpointManager implements EndpointManager{
	  private String _uddiURL;
	  private String _name;
	  private String _url;
	  private Endpoint _endpoint;
	  private UDDINaming _uddiNaming;
	  
	  private BrokerBackupPort _port;
	  
	  public BrokerBackupEndpointManager(String uddiURL, Broker broker) {
	    _uddiURL = uddiURL;
	    _endpoint = null;
	    _name = _url = null;
	    _uddiNaming = null;
	    _port = new BrokerBackupPort(broker);
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

	  @Override
	  public void connectToBackup(String backupName) throws JAXRException {
		  // the backup server does not have backups
		  return;  
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
	  
	  public BrokerBackupPort getPort(){
		  return _port;
	  }

	}
