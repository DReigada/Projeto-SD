package pt.upa.broker.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterCompaniesManager {

  private final static String UDDI_URL = "http://localhost:9090";
  private UDDINaming _uddiNaming; 
  private final static String TRANSPORTERS_NAME_REGEX = "UpaTransporter%";
  private Map<String, TransporterClient> _transporterCompaniesPorts;
  

  public TransporterCompaniesManager() {
    reconnectUDDI();
    _transporterCompaniesPorts = new HashMap<String, TransporterClient>();
  }

  public TransporterClient getTransporterPort(String name) throws JAXRException {
    String endpointAddress;
    try {
      endpointAddress = _uddiNaming.lookup(name);
    
    } catch (JAXRException e){
      reconnectUDDI();
      throw new JAXRException("Error connecting to juddi server.", e);
    }
    return _transporterCompaniesPorts.get(endpointAddress);
  }

  /***
   * Returns null if empty or if there was an error connect
   */
  public Collection<TransporterClient> getAllTransporterPorts() throws JAXRException {
    // get all transporter companies registered
    Collection<String> availableTransportersEndpoints = null;
    try {
      availableTransportersEndpoints = _uddiNaming.list(TRANSPORTERS_NAME_REGEX);

    } catch (JAXRException e){
      reconnectUDDI();
      throw new JAXRException("Error connecting to juddi server.", e);
    }
    
    if (availableTransportersEndpoints.isEmpty()) return null;

    // creates the return array of available services
    Map<String, TransporterClient> newTransporterCompaniesPorts = 
        new HashMap<String, TransporterClient>();

    TransporterClient transporter;

    // add service port from every available transporter service to the known services 
    for (String transporterEndpoint : availableTransportersEndpoints){

      // if port was already known simply add it to the knew known
      if (_transporterCompaniesPorts.containsKey(transporterEndpoint)) {
        transporter = _transporterCompaniesPorts.get(transporterEndpoint);

      } else {
    	  transporter = new TransporterClient(transporterEndpoint);
      }

      // add port to known registered transporters
      newTransporterCompaniesPorts.put(transporterEndpoint, transporter);
    }

    // swap the knew known transporter services with the previous
    _transporterCompaniesPorts = newTransporterCompaniesPorts;

    return newTransporterCompaniesPorts.values();
  }

  private void reconnectUDDI(){
    try {
      _uddiNaming = new UDDINaming(UDDI_URL);
    } catch (JAXRException e){
      System.err.println("Error creating uddiNaming object: " + e.getMessage());
    }
  }

}
