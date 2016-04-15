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
		client.clearTransports();
	}

	@After
	public void tearDown() {
		client.clearTransports();
	}

	// Test if the returned TransportView matches input Parameters and what it should be
	@Test
	public void testWithExistingID() throws UnknownTransportFault_Exception {

		int price = 32;
		String id = client.requestTransport("Lisboa", "Coimbra", price);

		TransportView transport1 = client.viewTransport(id);
		
		String id2 = client.requestTransport("Viseu", "Guarda", 32);
		TransportView transport2 = client.viewTransport(id2);
		
		client.requestTransport("Porto", "Faro", 1);
		TransportView transport3 = client.viewTransport("3");
		
		assertEquals("1", transport1.getId());
		assertEquals("Lisboa", transport1.getOrigin());
		assertEquals("Coimbra", transport1.getDestination());

		assertTrue(transport1.getPrice() <= price);
		assertEquals("UpaTransporter2", transport1.getTransporterCompany());

		assertTrue(transport1.getState() == TransportStateView.BOOKED);

		assertEquals("2", transport2.getId());
		assertEquals("Viseu", transport2.getOrigin());
		assertEquals("Guarda", transport2.getDestination());
		assertTrue(transport2.getPrice() > 0);
		assertEquals("UpaTransporter2", transport2.getTransporterCompany());
		assertTrue(transport2.getState() == TransportStateView.BOOKED);
		
		assertEquals("3", transport3.getId());
		assertEquals("Porto", transport3.getOrigin());
		assertEquals("Faro", transport3.getDestination());
		assertEquals(null, transport3.getPrice());
		assertEquals(null, transport3.getTransporterCompany());
		assertTrue(transport3.getState() == TransportStateView.FAILED);
		
	}
	
	@Test
	public void testStateCycle() throws UnknownTransportFault_Exception, InterruptedException {
		String id = client.requestTransport("Lisboa", "Coimbra", 31);		
		TransportView transport1;
		while(true){
			transport1 = client.viewTransport(id);
			Thread.sleep(1000);
			if(transport1.getState() == TransportStateView.HEADING){
				assertTrue(transport1.getState() == TransportStateView.HEADING);
				break;
			}
			if(transport1.getState() == TransportStateView.ONGOING){
				assertTrue(transport1.getState() == TransportStateView.ONGOING);
				break;
			}
				
			else if(transport1.getState() == TransportStateView.COMPLETED || transport1.getState() == TransportStateView.FAILED)
				break;
		}
		
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
	
	@Test
	public void testWithNullID(){
		String invalidID = null;
		try{
			client.viewTransport(invalidID);
			fail();
		} catch(UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.",e.getMessage());
		}
	}
	

}
