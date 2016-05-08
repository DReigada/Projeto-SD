package pt.upa.broker.ws;

import javax.xml.registry.JAXRException;

public interface EndpointManager {
	
	  public void start(String url);
	  
	  public void awaitConnections(String name) throws JAXRException;
	  
	  public void connectToBackup(String backupName) throws JAXRException;
	  
	  public void stop();
	  
}
