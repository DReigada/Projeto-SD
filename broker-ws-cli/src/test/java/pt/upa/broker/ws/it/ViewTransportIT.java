package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class ViewTransportIT extends BaseBrokerIT {

	@SuppressWarnings("unused")
	private String _id1 = null;
	@SuppressWarnings("unused")
	private String _id2 = null;


	// Set up a broker and request two transports
	@Before
	public void setUp() throws Exception {
		_id1 = client.requestTransport("Lisboa", "Coimbra", 50);
		_id2 = client.requestTransport("Viseu", "Aveiro", 80);

	}

	@After
	public void tearDown() {
		_id1 = null;
		_id2 = null;
	}

	// Test if the returned TransportView matches input parameters and what it should be
	@Test
	public void testWithExitingID() throws UnknownTransportFault_Exception {
		String[] transport1 = client.viewTransport("1");
		String[] transport2 = client.viewTransport("2");

		assertEquals("1", transport1[0]);
		assertEquals("Lisboa", transport1[1]);
		assertEquals("Coimbra", transport1[2]);
		assertTrue(Integer.parseInt(transport1[3]) > 0);
		assertEquals("1", transport1[4]);
		assertTrue(transport1[5] == TransportStateView.ONGOING.toString());

		assertEquals("2", transport2[0]);
		assertEquals("Viseu", transport2[1]);
		assertEquals("Aveiro", transport2[2]);
		assertTrue(Integer.parseInt(transport2[3]) > 0);
		assertEquals("2", transport2[4]);
		assertTrue(transport2[5] == TransportStateView.ONGOING.toString());
	}

	@Test
	public void testWithInvalidPrice() throws UnknownTransportFault_Exception {
		String[] transport1 = client.viewTransport("1");

		assertEquals("1", transport1[0]);
		assertEquals("Lisboa", transport1[1]);
		assertEquals("Coimbra", transport1[2]);
		assertTrue(Integer.parseInt(transport1[3]) > 0);
		assertEquals("1", transport1[4]);
		assertTrue(transport1[5] == TransportStateView.ONGOING.toString());

	}

	// Test if the return value is null when the ID is invalid
	@Test
	public void testWithInvalidID(){
		String invalidID = "not a valid ID";
		try{
			client.viewTransport(invalidID);
			fail();
		} catch(UnknownTransportFault_Exception e) {
			assertEquals("Unknown transport id",e.getMessage());
		}
	}


}
