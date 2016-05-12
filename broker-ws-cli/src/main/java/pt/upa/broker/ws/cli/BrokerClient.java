package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClient {

	BrokerPortType port;
	String[] _args;
	
	
	public String start(String[] args) {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
			return null;
		}
		_args = args;
		String uddiURL = args[0];
		String name = args[1];
		UDDINaming uddiNaming;
		String endpointAddress;

		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		try{
 			uddiNaming = new UDDINaming(uddiURL);
		} catch(IllegalArgumentException e){
			System.out.print("Invalid UDDI server URL");
			return null;
		} catch(javax.xml.registry.JAXRException e){
			System.out.print("Could not connect to UDDI server");
			return null;
		}

		System.out.printf("Looking for '%s'%n", name);

		try{
			endpointAddress = uddiNaming.lookup(name);
		} catch(javax.xml.registry.JAXRException e){
			System.out.print("Error looking up for the service");
			return null;
		}


		if (endpointAddress == null) {
			System.out.println("Not found!");
			return null;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		
		BrokerService service = new BrokerService();
		port = service.getBrokerPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		
		return endpointAddress;
	}
	
	private void reconnect() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			System.exit(1);
		}
		
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		String oldEndpointAddress = (String) requestContext.get(ENDPOINT_ADDRESS_PROPERTY);
		
		String newEndpointAddress = start(_args);
		
		if(!newEndpointAddress.equals(oldEndpointAddress)){
			return;
		}
		
		System.out.println("Failed getting the new Broker");
		System.exit(1);
	}

	public String ping (String pingOut) {
		try {
			return port.ping(pingOut);
		} catch (com.sun.xml.ws.client.ClientTransportException e) {
			reconnect();
	    	return port.ping(pingOut);
		}
	}
	
	public String requestTransport(String origin, String destination, int price) {
		int tries = 0;
		while(tries++ <= 1){
			try {
				return port.requestTransport(origin, destination, price);
			} catch (InvalidPriceFault_Exception e) {
				return "Booking failed: Invalid defined maximum price";
			} catch (UnavailableTransportFault_Exception e) {
				return "Booking failed: No transporter is available";
			} catch (UnavailableTransportPriceFault_Exception e) {
				return "Booking failed: No transporter is available within price range";
			} catch (UnknownLocationFault_Exception e) {
				return "Booking failed: Location is invalid";
			} catch (com.sun.xml.ws.client.ClientTransportException e) {
				reconnect();
				continue;
			}
		}
		return "Broker disconnected";
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		try {
			return port.viewTransport(id);
		} catch (com.sun.xml.ws.client.ClientTransportException e) {
			reconnect();
	    	return port.viewTransport(id);
		}
	}
	
	public List<TransportView> listTransports() {
		try {
			return port.listTransports();					
		} catch (com.sun.xml.ws.client.ClientTransportException e) {
			reconnect();
			return port.listTransports();
		}
	}
	
    public void clearTransports() {
		try {
			port.clearTransports();
		} catch (com.sun.xml.ws.client.ClientTransportException e) {
			reconnect();
	    	port.clearTransports();
		}
    }

}
