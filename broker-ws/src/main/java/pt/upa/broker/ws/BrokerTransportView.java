package pt.upa.broker.ws;

public class BrokerTransportView {
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
    
    public BrokerTransportView(TransportView transportView, String transporterID){
    	_transportView = transportView;
    	_transporterId = transporterID;
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