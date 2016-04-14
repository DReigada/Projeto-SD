package pt.upa.broker.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import pt.upa.broker.ws.cli.BrokerClient;

public abstract class BaseBrokerIT {
	private static final String TEST_PROP_FILE = "/test.properties";

	private static Properties props = null;
	protected static BrokerClient client = null;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		props = new Properties();
		try {
			props.load(BaseBrokerIT.class.getResourceAsStream(TEST_PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
		String uddiURL = props.getProperty("uddi.url");
		String wsName = props.getProperty("ws.name");
		
		client = new BrokerClient();
		String[] args = {uddiURL, wsName};
		client.start(args);

	}

	@AfterClass
	public static void cleanup() {
		client = null;
	}
}
