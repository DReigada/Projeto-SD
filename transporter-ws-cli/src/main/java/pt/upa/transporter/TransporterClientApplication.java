package pt.upa.transporter;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL transporterNumber%n", TransporterClientApplication.class.getName());
			return;
		}
		final String baseName = "UpaTransporter";
		
		String uddiURL = args[0];
		String transporterNumber = args[1];
		String name = baseName + transporterNumber;

		TransporterPortType port = null;
		
		try{
			System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);
	
			System.out.printf("Looking for '%s'%n", name);
			String endpointAddress = uddiNaming.lookup(name);
			
			if (endpointAddress == null) {
				System.out.println("Not found!");
				return;
			} else {
				System.out.printf("Found %s%n", endpointAddress);
			}
			
			System.out.println("Creating stub ...");
			TransporterService service = new TransporterService();
			port = service.getTransporterPort();
			
			System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
			
		}
		catch(Exception e){
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();
			System.exit(-1);
		}
		
		try{ 
			if(port != null){
				String test = "ping";
				System.out.println(port.ping(test));
			}
		}
		catch(Exception e){
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	private static void printViewInfo(JobView view) {
		System.out.println("----------------------------------");
		System.out.println("Transport ID: " + view.getJobIdentifier());
		System.out.println("Origin: " + view.getJobOrigin());
		System.out.println("Destination: " + view.getJobDestination());
		System.out.println("Price: " + view.getJobPrice());
		System.out.println("Transporter Company: " + view.getCompanyName());
		System.out.println("State: " + view.getJobState().name());
		System.out.println("----------------------------------");
		System.out.println();
	}
}
