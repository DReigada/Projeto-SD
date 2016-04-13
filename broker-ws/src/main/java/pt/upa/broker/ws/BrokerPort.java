package pt.upa.broker.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import javax.xml.registry.JAXRException;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;

import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;


@WebService(
  endpointInterface="pt.upa.broker.ws.BrokerPortType",
  wsdlLocation="broker.1_0.wsdl",
  name="BrokerWebService",
  portName="BrokerPort",
  targetNamespace="http://ws.broker.upa.pt/",
  serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType {

  private class BrokerTransportView {
    private String _transporterId;
    public TransportView _transportView;

    public BrokerTransportView(String id, String origin, String dest){
      _transporterId = "";

      TransportView t = new TransportView();
      t.setId(id);
      t.setOrigin(origin);
      t.setDestination(dest);
      t.setTransporterCompany("");
      t.setState(TransportStateView.REQUESTED);

      _transportView = t;
    }

    // Transport view attribute setters
    public void setTransportPrice(int price) { _transportView.setPrice(price); }
    public void setTransportCompany(String company) { _transportView.setTransporterCompany(company); }
    public void setTransportState(TransportStateView state) { _transportView.setState(state); }    

    // Transport view attribute getters
    public String getTransportCompany() { return _transportView.getTransporterCompany(); }
    public TransportStateView getTransportState() { return _transportView.getState(); }
    public TransportView getTransportView() { return _transportView; }

    // Id given by transporter company setter and getter
    public void setTransporterId(String id) { _transporterId = id; }
    public String getTransporterId() { return _transporterId; }
  }

  TransporterCompaniesManager _transportersManager;
  List<BrokerTransportView> _transports;

  public BrokerPort() {
    _transportersManager = new TransporterCompaniesManager();
    _transports = new ArrayList<BrokerTransportView>();
  }

  @Override
  public String ping(String name){
    return "Alive: " + name;
  }

  @Override
  public String requestTransport(String origin, String destination, int price) throws 
      UnknownLocationFault_Exception,
      InvalidPriceFault_Exception,
      UnavailableTransportFault_Exception,
      UnavailableTransportPriceFault_Exception {

    // TODO: check if origin and destination are known

    if (price < 0){ 
      InvalidPriceFault faultInfo = new InvalidPriceFault();
      faultInfo.setPrice(price);
      throw new InvalidPriceFault_Exception("Invalid price.", faultInfo);
    }

    Collection<TransporterPortType> temp;
    try {
      temp = _transportersManager.getAllTransporterPorts();

    } catch (JAXRException e) {
      UnavailableTransportFault faultInfo = new UnavailableTransportFault();
      faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
      throw new UnavailableTransportFault_Exception("Error connecting to transporters. PLease try again.", faultInfo);
    }
    List<TransporterPortType> transporters = new ArrayList<TransporterPortType>(temp);
    if (transporters == null){ 
      UnavailableTransportFault faultInfo = new UnavailableTransportFault();
      faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
      throw new UnavailableTransportFault_Exception("No transporter companies available.", faultInfo);
    }

    // saves transporter proposals status
    // 1 = no job for that price; 
    // 0 = no transporter between specified locations
    // -1 = at least one valid proposal
    int reason = 0; 

    int bestPrice = Integer.MAX_VALUE, bestJobIndex = 0;

    int previousNumberTransports = _transports.size();

    for (int i=0; i<transporters.size(); ++i){
      int thisIndex = previousNumberTransports+i;
      BrokerTransportView transport = new BrokerTransportView(thisIndex+"", origin, destination);
      _transports.add(transport);

      TransporterPortType port = transporters.get(i);

      JobView job;
      try {
        job = port.requestJob(origin, destination, price);
      } catch (BadLocationFault_Exception e) {
        _transports.set(thisIndex, null); 
        UnknownLocationFault faultInfo = new UnknownLocationFault();
        throw new UnknownLocationFault_Exception("Unknown origin or destination.", faultInfo);

      } catch (BadPriceFault_Exception e) {
        _transports.set(thisIndex, null); 
        InvalidPriceFault faultInfo = new InvalidPriceFault();
        faultInfo.setPrice(price);
        throw new InvalidPriceFault_Exception("Invalid price.", faultInfo);
      }

      if (job == null){
        _transports.set(thisIndex, null); 
        if (reason != -1) reason = 0; 
        continue;
      }
      
      transport.setTransportState(TransportStateView.BUDGETED);
      transport.setTransportCompany(job.getCompanyName());
      transport.setTransportPrice(job.getJobPrice());
      transport.setTransporterId(job.getJobIdentifier());

      int proposedPrice = job.getJobPrice();

      if (proposedPrice > price){ 
        transport.setTransportState(TransportStateView.FAILED);
        if (reason != -1) reason = 1; 
      }
      else reason = -1;
        
      if (proposedPrice < bestPrice) {
        bestPrice = proposedPrice;
        bestJobIndex = thisIndex;
      }
    }

    if (reason == 0) {
      UnavailableTransportFault faultInfo = new UnavailableTransportFault();
      faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
      throw new UnavailableTransportFault_Exception("No transport available for the requested route.", faultInfo);
    }
    if (reason == 1) {
      UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
      faultInfo.setBestPriceFound(bestPrice);
      throw new UnavailableTransportPriceFault_Exception("No transport available for the requested price.", faultInfo); 
    }

    for (int i=0; i<transporters.size(); ++i){
      int thisIndex = previousNumberTransports+i;
      BrokerTransportView transport = _transports.get(thisIndex);
      if (transport == null) continue;

      TransporterPortType port = transporters.get(i);
      boolean isAccepted = thisIndex == bestJobIndex;

      TransportStateView state 
          = (isAccepted ? TransportStateView.BOOKED : TransportStateView.FAILED);
      transport.setTransportState(state);
      
      try {
        port.decideJob(transport.getTransporterId(), isAccepted);
      } catch(BadJobFault_Exception e) {
        if (isAccepted) {
          UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
          throw new UnavailableTransportPriceFault_Exception("This exception" + 
            "should never happen. If it does it indicates a bug.", faultInfo); 
        }
      }
    }
    return bestJobIndex + "";
  }

  @Override
  public TransportView viewTransport(String id) throws 
      UnknownTransportFault_Exception {

    int index;
    try {
      index = Integer.parseInt(id);
    } catch (NumberFormatException e) {
      UnknownTransportFault faultInfo = new UnknownTransportFault();
      faultInfo.setId(id);
      throw new UnknownTransportFault_Exception("No transports match the given transport identifier.", faultInfo);
    }

    // check if id is invalid
    if (index >= _transports.size() || index < 0) {
      UnknownTransportFault faultInfo = new UnknownTransportFault();
      faultInfo.setId(id);
      throw new UnknownTransportFault_Exception("No transports match the given transport identifier.", faultInfo);
    }

    // get transport that matches the given id
    BrokerTransportView transport = _transports.get(index);
    if (transport == null) {
      UnknownTransportFault faultInfo = new UnknownTransportFault();
      faultInfo.setId(id);
      throw new UnknownTransportFault_Exception("No transports match the given transport identifier.", faultInfo);
    }
    
    // update state of transport in broker if transport is not COMPLETED or FAILED
    if ( transport.getTransportState() == TransportStateView.ONGOING 
          || transport.getTransportState() == TransportStateView.HEADING
          || transport.getTransportState() == TransportStateView.BUDGETED) {

      // gets the transporter company doing the transport
      TransporterPortType company;
      try {
        company = _transportersManager.getTransporterPort(transport.getTransportCompany());
      } catch (JAXRException e) {
        UnknownTransportFault faultInfo = new UnknownTransportFault();
        faultInfo.setId(id);
        throw new UnknownTransportFault_Exception("Failed to connect to the server. Please try again.", faultInfo);
      }

      // set transport to completed if company that made the transport is no longer in business
      if (company == null) transport.setTransportState(TransportStateView.COMPLETED);

      // gets the updated state of the transport from the company
      JobView job = company.jobStatus(transport.getTransporterId());
      
      TransportStateView state;
      try {
        state = convertToTransportStateView(job.getJobState());
      } catch (UnknownTransportFault_Exception e) {
        UnknownTransportFault faultInfo = new UnknownTransportFault();
        faultInfo.setId(id);
        throw new UnknownTransportFault_Exception("Invalid state" +
        " returned by the transporter company. Please try again.", faultInfo);
      }
      // finally updates the transport state in the broker
      transport.setTransportState(state);
    } 

    // returns the transport view to the client
    return transport.getTransportView();    
  }

  @Override
  public List<TransportView> listTransports() {
    List<TransportView> allTransports = new ArrayList<TransportView>();

    for (int i=0; i<_transports.size(); ++i) {
      try {
        TransportView transport = viewTransport(i+"");
        allTransports.add(transport);

      } catch (UnknownTransportFault_Exception e) {/* nothing */}
    }

    return allTransports;
  }

  @Override
  public void clearTransports() {
    List<TransporterPortType> companies;
    // get all the transporter companies
    try {
      Collection<TransporterPortType> temp = _transportersManager.getAllTransporterPorts();
      companies = new ArrayList<TransporterPortType>(temp);
    } catch (JAXRException e) {
      System.out.println("Error occurred connecting to juddi. Unable to clear " +
        "the transports. Please try again.");
      return;
    } 
    // for each company tell it to clear its jobs
    for (TransporterPortType port : companies) port.clearJobs();

    /* reset the Broker */
    // reset the Manager
    _transportersManager = new TransporterCompaniesManager();
    // clear the transports list
    _transports = new ArrayList<BrokerTransportView>();

  }

  private TransportStateView convertToTransportStateView(JobStateView state) throws 
      UnknownTransportFault_Exception {
    TransportStateView validState;
    switch (state) {
            case HEADING:   validState = TransportStateView.HEADING;
                            break;
            case ONGOING:   validState = TransportStateView.ONGOING;
                            break;
            case COMPLETED: validState = TransportStateView.COMPLETED;
                            break;
            default:  UnknownTransportFault faultInfo = new UnknownTransportFault();
                      throw new UnknownTransportFault_Exception("Invalid state" +
                        " returned by the transporter company.", faultInfo);
        }
    return validState;
  }
}
