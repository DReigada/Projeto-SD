package pt.upa.broker.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;


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
    Collection<TransporterClient> transporters;
    try {
      transporters = _transportersManager.getAllTransporterPorts();
    } catch (JAXRException e) {
      return name + ": Error connecting to transporters servers. Please try again.";
    }

    if (transporters == null) return name + ": No transporter companies available.";

    int count = 0;
    for (TransporterClient t : transporters) if (t.ping(name) == null) count++;

    int good = transporters.size() - count;
    return name + ": Connected successfully to " + good
      + " of " + transporters.size() + " transporter companies.";

  }

  @Override
  public String requestTransport(String origin, String destination, int price) throws 
      UnknownLocationFault_Exception,
      InvalidPriceFault_Exception,
      UnavailableTransportFault_Exception,
      UnavailableTransportPriceFault_Exception {


    if (price < 0){ 
      InvalidPriceFault faultInfo = new InvalidPriceFault();
      faultInfo.setPrice(price);
      throw new InvalidPriceFault_Exception("Invalid price.", faultInfo);
    }

    Collection<TransporterClient> temp;
    try {
      temp = _transportersManager.getAllTransporterPorts();

      if (temp == null){ 
        UnavailableTransportFault faultInfo = new UnavailableTransportFault();
        faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
        throw new UnavailableTransportFault_Exception("No transporter companies available.", faultInfo);
      }

    } catch (JAXRException e) {
      UnavailableTransportFault faultInfo = new UnavailableTransportFault();
      faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
      throw new UnavailableTransportFault_Exception("Error connecting to transporters. PLease try again.", faultInfo);
    }

    List<TransporterClient> transporters = new ArrayList<TransporterClient>(temp);
    

    // saves transporter proposals status
    // 1 = no job for that price; 
    // 0 = no transporter between specified locations
    // -1 = at least one valid proposal
    int reason = 0; 

    int bestPrice = Integer.MAX_VALUE, bestJobIndex = 0;
    JobView bestJob = null;

    int thisId = _transports.size() + 1;
    BrokerTransportView transport = new BrokerTransportView(thisId+"", origin, destination);
    transport.setTransportState(TransportStateView.REQUESTED);
    _transports.add(transport);

    JobView[] jobs = new JobView[transporters.size()];

    for (int i=0; i<transporters.size(); ++i){

      TransporterClient port = transporters.get(i);

      JobView job;
      try {
        job = port.requestJob(origin, destination, price);
      } catch (BadLocationFault_Exception e) {
        transport.setTransportState(TransportStateView.FAILED); 
        UnknownLocationFault faultInfo = new UnknownLocationFault();
        faultInfo.setLocation(e.getFaultInfo().getLocation());
        throw new UnknownLocationFault_Exception("Unknown origin or destination.", faultInfo);

      } catch (BadPriceFault_Exception e) {
        transport.setTransportState(TransportStateView.FAILED); 
        InvalidPriceFault faultInfo = new InvalidPriceFault();
        faultInfo.setPrice(price);
        throw new InvalidPriceFault_Exception("Invalid price.", faultInfo);
      }

      if (job == null) continue;

      jobs[i] = job;

      int proposedPrice = job.getJobPrice();

      reason = (proposedPrice > price && reason != -1 ? 1 : -1);
        
      if (proposedPrice < bestPrice) {
        bestPrice = proposedPrice;
        bestJob = job;
        bestJobIndex = i;
      }
    }

    if (reason == 0) {
      transport.setTransportState(TransportStateView.FAILED);
      UnavailableTransportFault faultInfo = new UnavailableTransportFault();
      faultInfo.setOrigin(origin); faultInfo.setDestination(destination);
      throw new UnavailableTransportFault_Exception("No transport available for the requested route.", faultInfo);
    }

    transport.setTransportState(TransportStateView.BUDGETED);

    for (int i=0; i<transporters.size(); ++i){

      TransporterClient port = transporters.get(i);
      JobView job = jobs[i];

      if (job == null) continue;

      boolean isAccepted = reason == -1 && i == bestJobIndex;
      
      try {
        port.decideJob(job.getJobIdentifier(), isAccepted);
      } catch(BadJobFault_Exception e) {
        if (isAccepted) {
          UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
          throw new UnavailableTransportPriceFault_Exception("This exception" + 
            "should never happen. If it does it indicates a bug.", faultInfo); 
        }
      }
    }

    if (reason == -1){ 
      transport.setTransportCompany(bestJob.getCompanyName());
      transport.setTransportPrice(bestJob.getJobPrice());
      transport.setTransporterId(bestJob.getJobIdentifier());
      transport.setTransportState(TransportStateView.BOOKED);

    } else {
      transport.setTransportState(TransportStateView.FAILED);
      UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
      faultInfo.setBestPriceFound(bestPrice);
      throw new UnavailableTransportPriceFault_Exception("No transport available for the requested price.", faultInfo); 
    }

    return thisId+"";
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

    --index; //converts index to index the transports list

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
          || transport.getTransportState() == TransportStateView.BOOKED) {

      // gets the transporter company doing the transport
      TransporterClient company;
      try {
        company = _transportersManager.getTransporterPort(transport.getTransportCompany());
      } catch (JAXRException e) {
        UnknownTransportFault faultInfo = new UnknownTransportFault();
        faultInfo.setId(id);
        throw new UnknownTransportFault_Exception("Failed to connect to the server. Please try again.", faultInfo);
      }

      // set transport to completed if company that made the transport is no longer in business
      if (company == null){
    	  transport.setTransportState(TransportStateView.COMPLETED);
    	  return transport.getTransportView();
      }

      else {
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
    }
    // returns the transport view to the client
    return transport.getTransportView();    
  }

  @Override
  public List<TransportView> listTransports() {
    List<TransportView> allTransports = new ArrayList<TransportView>();

    for (int i=0; i<_transports.size(); ++i) {
      try {
        TransportView transport = viewTransport(i+1+"");
        allTransports.add(transport);

      } catch (UnknownTransportFault_Exception e) {/* nothing */}
    }

    return allTransports;
  }

  @Override
  public void clearTransports() {
    List<TransporterClient> companies;
    // get all the transporter companies
    try {
      Collection<TransporterClient> temp = _transportersManager.getAllTransporterPorts();
      companies = new ArrayList<TransporterClient>(temp);
    } catch (JAXRException e) {
      System.out.println("Error occurred connecting to juddi. Unable to clear " +
        "the transports. Please try again.");
      return;
    } 
    // for each company tell it to clear its jobs
    for (TransporterClient port : companies) port.clearJobs();

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
            case ACCEPTED: validState = TransportStateView.BOOKED;
                            break;
            default:  UnknownTransportFault faultInfo = new UnknownTransportFault();
                      throw new UnknownTransportFault_Exception("Invalid state" +
                        " returned by the transporter company.", faultInfo);
        }
    return validState;
  }
}
