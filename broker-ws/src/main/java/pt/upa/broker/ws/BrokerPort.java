package pt.upa.broker.ws;

import javax.jws.WebService;

import java.util.List;

import java.util.Map;
import java.util.HashMap;

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
      t.setCompany("");
      t.setState(transportStateView.REQUESTED);

      _transportView = t;
    }

    public void setTransportState(transportStateView state) { _transportView.setState(state); }
    public void setTransportCompany(String company) { _transportView.setCompany(company); }
    public void setTransportPrice(int price) { _transportView.setPrice(price); }
    public void setTransporterId(String id) { _transporterId = id; }
  }

  TransporterCompaniesManager _transportersManager;
  List<BrokerTransportView> _transports;

  public BrokerPort() {
    _transportersManager = new TransporterCompaniesManager();
    _transports = new ArrayList<TransportView>();
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

    if (price < 0) throw new InvalidPriceFault_Exception();

    int nextId = _transports.size();

    Collection<TransporterPortType> transporters = TransporterCompaniesManager.getAll();
    if (transporters == null) throw new UnavailableTransportFault_Exception();

    // saves transporter proposals status
    // 1 = no job for that price; 
    // 0 = no transporter between specified locations
    // -1 = at least one valid proposal
    int reason = 0; 

    int bestPrice = Integer.MAX_VALUE, bestJobIndex;

    JobView[] proposedJobs = new JobView[transporters.size()];

    for (int i=0; i<transporters.size(); ++i){
      BrokerTransportView t = new BrokerTransportView(_transports.size()+"", origin, destination, price);
      _transports.add(t);

      TransporterPortType port = transporters.get(i);

      try {
        JobView job = port.requestJob(origin, destination, price);
      } catch (BadJobFault_Exception e) {
        throw new UnknownLocationFault_Exception();
      }
      proposedJobs[i] = job;

      if (job == null){
        _transports.set(_transports.size()-1, null); 
        if (reason != -1) reason = 0; 
        continue;
      }

      _transports.setTransportState(transportStateView.BUDGETED);
      _transports.setTransportCompany(job.getCompanyName());
      _transports.setTransportPrice(job.getJobPrice());
      _transports.setTransporterId(job.getJobId());

      proposedPrice = job.getJobPrice();

      if (proposedPrice > price){  
        if (reason != -1) reason = 1; 
        continue;
      }

      if (price < bestPrice){ 
        reason = -1;
        bestPrice = price;
        bestJobIndex = i;
      }
    }

    if (reason == 0) throw new UnavailableTransportFault_Exception();
    if (reason == 1) throw new UnavailableTransportPriceFault_Exception();

    for (int i=0; i<transporters.size(); ++i){
      if (proposedJobs[i] == null) continue;

      TransporterPortType port = transporters.get(i);
      boolean isAccepted = i == bestJobIndex;

      transportStateView state 
          = (isAccepted ? transportStateView.BOOKED : transportStateView.FAILED);
      _transports.setTransportState(state);
      
      port.decideJob(proposedJobs[i], isAccepted);
    }

    return nextId + bestJobIndex + "";
  }

  @Override
  public TransportView viewTransport(String id) throws 
      UnknownLocationFault_Exception {

  }

  @Override
  public TransportView[] listTransports() {

  }

  @Override
  public void clearTransports() {

  }
}
