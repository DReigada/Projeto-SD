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

public class RequestViewTransportIT extends BaseBrokerIT {

	@SuppressWarnings("unused")
	private String _id1 = null;



	@Before
	public void setUp() throws Exception {
		client.clearTransports();
		_id1 = client.requestTransport("Lisboa", "Coimbra", 50);
		
	}

	@After
	public void tearDown() {
		_id1 = null;

	}

	@Test
	public void testWithExistingID() throws UnknownTransportFault_Exception {
		TransportView transport1 = client.viewTransport("0");
		
		assertEquals("0", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Coimbra", transport1.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.FAILED);

	}
	
	





}
