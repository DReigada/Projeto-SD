package pt.upa.broker.ws;

import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.xml.registry.JAXRException;

// wsimport generated classes
import pt.upa.transporter.ws.TransporterService;
import pt.upa.transporter.ws.TransporterPortType;

public class TransporterCompaniesManager {

  private final static String UDDI_URL = "http://localhost:9090";
  private UDDINaming _uddiNaming; 
  private final static String TRANSPORTERS_NAME_REGEX = "UpaTransporter%";
  private Map<String, TransporterPortType> _transporterCompaniesPorts;
  

  public TransporterCompaniesManager() {
    reconnectUDDI();
    _transporterCompaniesPorts = new HashMap<String, TransporterPortType>();
  }

  public TransporterPortType getTransporterPort(String name) throws JAXRException {
    // TODO
    try {
      String endpointAddress = _uddiNaming.lookup(name);
    
    } catch (JAXRException e){
      reconnectUDDI();
      throw new JAXRException("Error connecting to juddi server.", e);
    }
    return _transporterCompaniesPorts.get(endpointAddress);
  }

  /***
   * Returns null if empty or if there was an error connect
   */
  public Collection<TransporterPortType> getAllTransporterPorts() throws JAXRException {
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
    Map<String, TransporterPortType> newTransporterCompaniesPorts = 
        new HashMap<String, TransporterPortType>();

    TransporterPortType port;

    // add service port from every available transporter service to the known services 
    for (String transporterEndpoint : availableTransportersEndpoints){

      // if port was already known simply add it to the knew known
      if (_transporterCompaniesPorts.containsKey(transporterEndpoint)) {
        port = _transporterCompaniesPorts.get(transporterEndpoint);

      } else {
        // get new port
        TransporterService service = new TransporterService();
        port = service.getTransporterPort();

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, transporterEndpoint);
      }

      // add port to known registered transporters
      newTransporterCompaniesPorts.put(transporterEndpoint, port);
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
