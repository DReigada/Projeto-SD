package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class clearTransportsIT extends BaseBrokerIT {
	
	@Test
    public void testUnknownDestination() throws Exception {
    	
    	String t1 = client.requestTransport("Lisboa", "Coimbra", 30);
    	String t2 = client.requestTransport("Lisboa", "Leiria", 20);
    	String t3 = client.requestTransport("Lisboa", "Aveiro", 40);
		
    	client.clearTransports();
    	
    	assertEquals(0, client.listTransports().size());
		
    }
}
