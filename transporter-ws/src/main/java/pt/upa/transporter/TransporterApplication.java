package pt.upa.transporter;

import pt.upa.transporter.core.Transporter;
import pt.upa.transporter.ws.TransporterEndpoint;

public class TransporterApplication {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL transporterNumber endpointURL%n", TransporterApplication.class.getName());
			return;
		}
		
		System.out.println(TransporterApplication.class.getSimpleName() + " starting...");

		
		String uddiURL = args[0];
		String ID = args[1];
		String endpointURL = args[2];	
		
		Transporter transporter = new Transporter(Integer.parseInt(ID));
		TransporterEndpoint endpoint = new TransporterEndpoint(transporter, endpointURL, uddiURL);
		
		System.out.printf("Publishing %s endpoint at %s%n", transporter.getName(), endpointURL);
		endpoint.startEndpoint();
		System.out.printf("Binding endpoint at %s%n", uddiURL);
		endpoint.bind();
		
		System.out.printf("Awaiting requests%nPress Enter to shutdown...");
		System.in.read();
		
		System.out.printf("Unbinding endpoint from %s%n", uddiURL);
		endpoint.unbind();
		System.out.printf("Stoping endpoint%n");
		endpoint.stopEndpoint();
		
		System.out.printf("%s ended successfully%n", transporter.getName());
	}

}
