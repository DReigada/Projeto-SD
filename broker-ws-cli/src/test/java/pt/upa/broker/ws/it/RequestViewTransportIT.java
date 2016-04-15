package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class RequestViewTransportIT extends BaseBrokerIT {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() {
		client.clearTransports();
	}

	@Test
	public void testWithExistingID() throws UnknownTransportFault_Exception {
		int price = 31;
		String id = client.requestTransport("Lisboa", "Coimbra", price);
		TransportView transport1 = client.viewTransport(id);
		
		assertEquals("1", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Coimbra", transport1.getDestination());
		assertTrue(transport1.getPrice() <= price);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.BOOKED);

	}
	
	





}
