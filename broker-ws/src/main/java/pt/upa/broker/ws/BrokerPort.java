package pt.upa.broker.ws;

import javax.jws.WebService;

@WebService(
  endpointInterface="pt.upa.broker.ws.BrokerPortType",
  wsdlLocation="broker.1_0.wsdl",
  name="BrokerWebService",
  portName="BrokerPort",
  targetNamespace="http://ws.broker.upa.pt/",
  serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType {

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

  }

  @Override
  public TransportView viewTransport(String id) throws 
    UnknownLocationFault_Exception {

  }

  @Override
  public Iterable listTransports() {

  }

  @Override
  public void clearTransports() {

  }
}
