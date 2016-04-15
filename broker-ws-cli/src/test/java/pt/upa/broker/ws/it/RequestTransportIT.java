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
		String reply = client.requestTransport("Faro", "Porto", 8);
		assertEquals("Booking failed: No transporter is available", reply);
		
    }
    
    //test when the best price is greater than the reference price
    @Test
    public void testNoTransportAvailableForGivenPrice() throws Exception{
		String reply = client.requestTransport("Faro", "Lisboa", 30);
		assertEquals("Booking failed: No transporter is available within price range", reply);
		
    }
   
    // First id of valid price must be 1
    @Test
    public void testReturnedTransportId() throws Exception{
		String reply = client.requestTransport("Lisboa", "Lisboa", 8);
		assertEquals("1", reply);
    }
    
    // Test valid accentuated city names
    @Test
    public void testAccentuatedCityName() throws Exception{
    	String accentuatedCity = "Setúbal";
    	String reply = client.requestTransport("Lisboa", accentuatedCity, 8);
		assertEquals("1", reply);
    }
    
    // Test valid city names with spaces
    @Test
    public void testMultipleWordedCityName() throws Exception{
    	String multipleWordedCity = "Castelo Branco";
		String reply = client.requestTransport("Lisboa", multipleWordedCity, 8);
		assertEquals("1", reply);
    }
    
}
