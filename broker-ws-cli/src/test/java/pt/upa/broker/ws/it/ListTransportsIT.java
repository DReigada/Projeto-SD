package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import pt.upa.broker.ws.TransportView;

public class ListTransportsIT extends BaseBrokerIT {
	/**
     * The following tests test if the broker throws 
     * UnknownLocationFault_Exception if the origin/destination is not valid
     */
    @Test
    public void testListTransports() throws Exception {
    	
    	
		String t1 = client.requestTransport("Lisboa", "Coimbra", 10);
		String t2 = client.requestTransport("Lisboa", "Aveiro", 30);
		String t3 = client.requestTransport("Lisboa", "Leiria", 40);
		
		List<TransportView> test_transports = client.listTransports();
		
		TransportView[] actual_transports = {
				client.viewTransport(t1),
				client.viewTransport(t2),
				client.viewTransport(t3)
		};
		
		assertEquals(3, test_transports.size());
		
		for (int i=0; i<3; ++i) {
			TransportView test = test_transports.get(i);
			TransportView actual = actual_transports.get(i);
			
			assertEquals(actual.getDestination(), test.getDestination());
			assertEquals(actual.getPrice(), test.getPrice());
			assertEquals(actual.getTransporterCompany(), test.getTransporterCompany());
			assertEquals(actual.getId(), test.getId());
		}
		
    }
}
