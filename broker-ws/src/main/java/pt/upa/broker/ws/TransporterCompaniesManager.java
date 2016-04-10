package pt.upa.broker.ws;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.xml.registry.JAXRException;

public class TransporterCompaniesManager {

  private final static String UDDI_URL = "http://localhost:9090";
  private UDDINaming _uddiNaming; 

  private final static String TRANSPORTERS_NAME_REGEX = "UpaTransporter%";

  private Map<String, TransporterPortType> _transporterCompaniesPorts;
  

  public TransporterCompaniesManager() {
    _uddiNaming = new UDDINaming(UDDI_URL);
    _transporterCompaniesPorts = new HashMap<String, TransporterPortType>();
  }

  public TransporterPortType getTransporterPort(String name){
    try {
      String endpointAddress = uddiNaming.lookup(name);
    
    } catch (JAXRException e){
      reconnectUDDI();
      return null;
    }
    return _transporterCompaniesPorts.get(endpointAddress);
  }

  public Collection<TransporterPortType> getAllTransporterPorts(){
    // get all transporter companies registered
    try {
      Collection<String> availableTransportersEndpoints = _uddiNaming.list(TRANSPORTERS_NAME_REGEX);

    } catch (JAXRException e){
      reconnectUDDI();
      return null;
    }
    
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
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
      }

      // add port to known registered transporters
      newTransporterCompaniesPorts.put(transporterEndpoint, port);
    }

    // swap the knew known transporter services with the previous
    _transporterCompaniesPorts = newTransporterCompaniesPorts;

    return newTransporterCompaniesPorts.values();
  }

  private void reconnectUDDI(){
    _uddiNaming = new UDDINaming(UDDI_URL);
  }

}
