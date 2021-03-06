package pt.upa.broker;

import java.util.InputMismatchException;
import java.util.Scanner;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	static BrokerClient client;


	public static void main(String[] args) throws Exception {
		client = new BrokerClient();

		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		while(true) {
			System.out.println("Connect to booking system? (y/n)");
			String option = input.next();
			switch (option) {
			case "y":
				System.out.println("connecting...");
				client.start(args);
				int i = menu(client);
				if (i == 9)
					return;
				break;
			case "n":
				System.out.println("ok... bye");
				return;
			default:
				System.out.println("invalid option, please select 'y' or 'n'");
				break;
			}
		}

	}

	private static int menu(BrokerClient client) {

		while(true) {

			System.out.println("Select option (1-6)");
			System.out.println("1 Request Transport");
			System.out.println("2 List transport");
			System.out.println("3 Clear Transports");
			System.out.println("4 View Transport");
			System.out.println("5 Ping");
			System.out.println("6 Quit");


			@SuppressWarnings("resource")
			Scanner input = new Scanner(System.in);
			int option = input.nextInt();
			switch (option) {
			case 1:
				request();
				break;
			case 2:
				list();
				break;
			case 3:
				clear();
				break;
			case 4:
				view();
				break;
			case 5:
				ping();
				break;
			case 6:
				System.out.println("BYE...");
				return 9;
			default:
				System.out.println("invalid option");
				break;
			}
		}
	}

	/*
	 * MAIN OPERATIONS: REQUEST TRANSPORT, VIEW TRANSPORT
	 */
	private static void request() {
		//transport data input from user
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("TRANSPORT REQUEST FORM");
		System.out.println("Origin: ");
		String origin = input.nextLine();
		System.out.println("Destination: ");
		String destination = input.nextLine();
		System.out.println("Maximum price:");
			
		int price = 0;
		while (true) {
			try {
				price = input.nextInt();
				break;
			}
			catch (InputMismatchException e) {
				input.next();
				System.out.println("Insert a valid price (integer): ");
			}
		}

		//method call
		String reply = client.requestTransport(origin, destination, price);
		//response from broker
		System.out.println(reply);
	}

	private static void view() {
		//id input from user
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("Please insert the id of the transport service in question:");
		String id = input.next();
		//method call. it may fail if the transport id requested does not exist
		try {
			//calls printTransportInfo method that organises and prints the requested info
			TransportView transport = client.viewTransport(id);
			printTransportInfo(transport);
		} catch (UnknownTransportFault_Exception e) {
			System.out.println("Unknown transport id");
		}
	}



	/*
	 * AUXILIARY OPERATIONS: PING, LIST TRANPORTS, CLEAR TRANSPORTS
	 */
	private static void ping() {
		System.out.println("Client asking: Are you alive?");
		System.out.println("Broker reply: " + client.ping("Are you alive?"));
	}

	private static void list() {
		for(TransportView element : client.listTransports()){
			printTransportInfo(element);
		}

	}

	private static void clear() {
		client.clearTransports();
		System.out.println("All transports have been eliminated");
	}


	//auxiliary method to print the transport info to the screen
	private static void printTransportInfo(TransportView transport) {
		System.out.println("----------------------------------");
		System.out.println("Transport ID: " + transport.getId());
		System.out.println("Origin: " + transport.getOrigin());
		System.out.println("Destination: " + transport.getDestination());
		System.out.println("Price: " + transport.getPrice());
		System.out.println("Transporter Company: " + transport.getTransporterCompany());
		System.out.println("State: " + transport.getState());
		System.out.println("----------------------------------");
		System.out.println();
	}

}
