package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.text.BadLocationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class RequestViewTransportIT extends BaseBrokerIT {

	@Before
	public void setUp() throws Exception {
		client.clearTransports();		
	}

	@After
	public void tearDown() {
		client.clearTransports();
	}

	@Test
	public void testWithExistingID() throws UnknownTransportFault_Exception {
		String id = client.requestTransport("Lisboa", "Coimbra", 31);
		TransportView transport1 = client.viewTransport(id);

		assertEquals("1", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Coimbra", transport1.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.BOOKED);
	}
	
	@Test
	public void testTwoNameCity() throws UnknownTransportFault_Exception {
		String id = client.requestTransport("Lisboa", "Castelo Branco", 31);
		TransportView transport1 = client.viewTransport(id);

		String id2 = client.requestTransport("Castelo Branco", "Lisboa", 31);
		TransportView transport2 = client.viewTransport(id2);
		
		assertEquals("1", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Castelo Branco", transport1.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.BOOKED);


		assertEquals("2", transport2.getId());
		assertEquals("Castelo Branco", transport2.getOrigin());
		assertEquals("Lisboa", transport2.getDestination());
		assertTrue(transport2.getPrice() > 0);
		assertEquals("UpaTransporter1", transport2.getTransporterCompany());
		assertTrue(transport2.getState() == TransportStateView.BOOKED);

	}


	@Test
	public void testAccentuatedCity() throws UnknownTransportFault_Exception {
		String id = client.requestTransport("Lisboa", "Évora", 31);
		TransportView transport1 = client.viewTransport(id);

		String id2 = client.requestTransport("Évora", "Lisboa", 31);
		TransportView transport2 = client.viewTransport(id2);
		
		assertEquals("1", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Évora", transport1.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport1.getTransporterCompany());
		assertTrue(transport1.getState() == TransportStateView.BOOKED);

		assertEquals("2", transport2.getId());
		assertEquals("Évora", transport2.getOrigin());
		assertEquals("Lisboa", transport2.getDestination());
		assertTrue(transport1.getPrice() > 0);
		assertEquals("UpaTransporter1", transport2.getTransporterCompany());
		assertTrue(transport2.getState() == TransportStateView.BOOKED);
	}
/*
	@Test
	public void testNullOrigin() {
		String nullCity = null;
		try{
			String id = client.requestTransport(nullCity, "Coimbra", 31);
			client.viewTransport(id);
			fail();
		} catch(BadLocationException e) {
			assertEquals("The origin city is invalid",e.getMessage());
		}
	}
	
	@Test
	public void testNullDestination() {
		String nullCity = null;
		try{
			String id = client.requestTransport("Lisboa",nullCity, 31);
			client.viewTransport(id);
			fail();
		} catch(UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.",e.getMessage());
		}
	}

*/
}
