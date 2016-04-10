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
	
	
	public void start(String[] args) {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		UDDINaming uddiNaming;
		String endpointAddress;

		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		try{
 			uddiNaming = new UDDINaming(uddiURL);
		} catch(IllegalArgumentException e){
			System.out.print("Invalid UDDI server URL");
			return;
		} catch(javax.xml.registry.JAXRException e){
			System.out.print("Could not connect to UDDI server");
			return;
		}

		System.out.printf("Looking for '%s'%n", name);

		try{
			endpointAddress = uddiNaming.lookup(name);
		} catch(javax.xml.registry.JAXRException e){
			System.out.print("Error looking up for the service");
			return;
		}


		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
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
			
		
	}
	
	public String ping (String pingOut) {
		String pingIn = port.ping(pingOut);
		return pingIn;
	}
	
	public String requestTransport(String origin, String destination, int price) {
		String reply;
		try {
			reply = port.requestTransport(origin, destination, price);
		} catch (InvalidPriceFault_Exception e) {
			reply = "Booking failed: Invalid defined maximum price";
			e.printStackTrace();
		} catch (UnavailableTransportFault_Exception e) {
			reply = "Booking failed: No transporter is available";
			e.printStackTrace();
		} catch (UnavailableTransportPriceFault_Exception e) {
			reply = "Booking failed: No transporter is available within price range";
			e.printStackTrace();
		} catch (UnknownLocationFault_Exception e) {
			reply = "Booking failed: Location is invalid";
			e.printStackTrace();
		}
		return reply;
	}
	
	public String[] viewTransport(String id) throws UnknownTransportFault_Exception {
		TransportView transportView = port.viewTransport(id);
		//call the method to get all the info about the transport
		//pass the array of strings to the client application, for display
		return getTransportInfo(transportView);
	}
	
	public String[] getTransportInfo(TransportView transportView){
		String[] transport = new String[6];
		
		//populate an array of strings with the return values 
		transport[0] = transportView.getId();
		transport[1] = transportView.getOrigin();
		transport[2] = transportView.getDestination();
		transport[3] = transportView.getPrice().toString();
		transport[4] = transportView.getTransporterCompany();
		transport[5] = transportView.getState().toString();
		
		return transport;
	}
	
	public List<TransportView> listTransports() {
		List<TransportView> transportList = port.listTransports();		
		return transportList;
	}
	
    public void clearTransports() {
    	port.clearTransports();
    }

}
