package pt.upa.broker;

import pt.upa.broker.ws.BrokerEndpointManager;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");

    // Check arguments
    if (args.length < 3) {
      System.err.println("Argument(s) missing!");
      System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
      return;
    }

    String uddiURL = args[0];
    String name = args[1];
    String url = args[2];

    BrokerEndpointManager brokerEndPointManager = new BrokerEndpointManager(uddiURL);

    try {

      // publish endpoint
      System.out.printf("Starting %s%n", url);
      brokerEndPointManager.start(url);

      // publish to UDDI
      System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
      brokerEndPointManager.awaitConnections(name);

      // wait
      System.out.println("Awaiting connections");
      System.out.println("Press enter to shutdown");
      System.in.read();

    } catch (Exception e) {
      System.out.printf("Caught exception: %s%n", e);
      e.printStackTrace();

    } finally {
      brokerEndPointManager.stop();
    }
  }
}
