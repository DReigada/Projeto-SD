package pt.upa.broker.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PingIT extends BaseBrokerIT {

	String pingMsg;


	@Before
	public void setUp() throws Exception {
		pingMsg = "Hello";
	}

	@After
	public void tearDown() {
		pingMsg = null;
	}

	// ping test
	@Test
	public void testPing() {
		Object reply = client.ping(pingMsg);
		assertTrue(reply instanceof String);
	}
}
