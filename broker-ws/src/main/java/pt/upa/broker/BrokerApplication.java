package pt.upa.broker;

import pt.upa.broker.ws.Broker;

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

    Broker broker = new Broker(uddiURL, name, url, false);

    try {

      broker.start();
      
      // wait
      System.out.println("Awaiting connections");
      System.out.println("Press enter to shutdown");
      System.in.read();

    } catch (Exception e) {
      System.out.printf("Caught exception: %s%n", e);
      e.printStackTrace();

    } finally {
      broker.stop();
    }
  }
}
