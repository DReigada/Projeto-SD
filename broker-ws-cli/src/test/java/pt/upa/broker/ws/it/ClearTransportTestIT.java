package pt.upa.broker.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClearTransportTestIT extends BaseBrokerIT {

	@SuppressWarnings("unused")
	private String _id1 = null;



	@Before
	public void setUp() throws Exception {
		client.clearTransports();
	}

	@After
	public void tearDown() {
		_id1 = null;

	}

	@Test
	public void testClear() {
		client.requestTransport("Lisboa", "Coimbra", 50);
		client.requestTransport("Lisboa", "Coimbra", 50);
		assertTrue(client.listTransports().isEmpty() == false);
		client.clearTransports();
		assertTrue(client.listTransports().isEmpty() == true);
	}
	
	@Test
	public void testEmptyClear() {
		assertTrue(client.listTransports().isEmpty() == true);
		client.clearTransports();
		assertTrue(client.listTransports().isEmpty() == true);
	}





}
