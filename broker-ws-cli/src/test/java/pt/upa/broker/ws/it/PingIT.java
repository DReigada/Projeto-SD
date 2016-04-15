package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

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
