package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RequestTransportIT extends BaseBrokerIT{
	// tests
	// assertEquals(expected, actual);

	/**
     * The following tests test if the broker throws 
     * UnknownLocationFault_Exception if the origin/destination is not valid
     */
    @Test
    public void testUnknownOrigin() throws Exception {
    	String badCityName = "not a city";
    	
		String reply = client.requestTransport(badCityName, "Lisboa", 10);
		assertEquals("Booking failed: Location is invalid", reply);
		
    }
    
    @Test
    public void testUnknownDestination() throws Exception {
    	String badCityName = "not a city";
    	
		String reply = client.requestTransport("Lisboa", badCityName, 10);
		assertEquals("Booking failed: Location is invalid", reply);
		
    }
    
    // < 0
    @Test
    public void testPriceEqualZero() throws Exception{
    	int badPrice = -1;
		String reply = client.requestTransport("Lisboa", "Lisboa", badPrice);
		assertEquals("Booking failed: Invalid defined maximum price", reply);
		
    }
    
    // Reference price > 100 (no available transports fault)
    @Test
    public void testPriceGreaterThanHundred() throws Exception{
    	int badPrice = 101;
		String reply = client.requestTransport("Lisboa", "Lisboa", badPrice);
		assertEquals("Booking failed: No transporter is available", reply);
		
    }
    
    // No transporters operate in the given locations
    @Test
    public void testNoTransportAvailableForLocations() throws Exception{
		String reply = client.requestTransport("Faro", "Porto", 30);
		assertEquals("Booking failed: No transporter is available", reply);
		
    }
    
    // First id of valid price must be 1
    @Test
    public void testReturnedTransportId() throws Exception{
		String reply = client.requestTransport("Lisboa", "Lisboa", 30);
		assertEquals("1", reply);
    }
}
