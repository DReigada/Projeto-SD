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

public class ViewTransportIT extends BaseBrokerIT {


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() {
		client.clearTransports();
	}

	// Test if the returned TransportView matches input parameters and what it should be
	@Test
	public void testWithExistingID() throws UnknownTransportFault_Exception {
		
		TransportView transport1 = client.viewTransport("0");
		
		assertEquals("0", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Coimbra", transport1.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.BOOKED);

	}


	// Test if the return value is null when the ID is invalid
	@Test
	public void testWithInvalidID(){
		String invalidID = "not a valid ID";
		try{
			client.viewTransport(invalidID);
			fail();
		} catch(UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.",e.getMessage());
		}
	}
	

}
