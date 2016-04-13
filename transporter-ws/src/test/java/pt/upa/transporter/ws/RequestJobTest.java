package pt.upa.transporter.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.core.Transporter;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public abstract class RequestJobTest {
	
	private int _ID;
	
	protected RequestJobTest(int transporterID) {
		_ID = transporterID;
	}
    
	protected TransporterPort _port = null;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	_port = new TransporterPort(new Transporter(_ID));
    }

    @After
    public void tearDown() {
    	_port = null;
    }

    // Test the returned JobView
    @Test
    public void testReturnedJobView() throws Exception{
    	String 	city1 = "Lisboa",
    			city2 = "Coimbra";
    	
    	JobView job = _port.requestJob(city1, city2, 50);
    	assertEquals(city1, job.getJobOrigin());
    	assertEquals(city2, job.getJobDestination());
    	assertEquals("UpaTransporter" + _ID, job.getCompanyName());
    	assertEquals("1", job.getJobIdentifier());
    	assertTrue(job.getJobPrice() > 0);
    	assertEquals(JobStateView.PROPOSED, job.getJobState());
    	
    	job = _port.requestJob(city2, city1, 10);
    	assertEquals(city2, job.getJobOrigin());
    	assertEquals(city1, job.getJobDestination());
    	assertEquals("UpaTransporter" + _ID, job.getCompanyName());
    	assertEquals("2", job.getJobIdentifier());
    	assertTrue(job.getJobPrice() > 0);
    	assertEquals(JobStateView.PROPOSED, job.getJobState());
    }

    // Location related tests
	
    /**
     * The following tests test if the transporter returns null if the 
     * origin/destination given is outside their operation region
     */
	@Test
	public void testOriginInNorthRegion() throws Exception {
		assertEquals(_ID % 2 != 0, _port.requestJob("Porto", "Lisboa", 10) == null);
	}
	
	@Test
    public void testOriginInCentreRegion() throws Exception {
		assertNotNull(_port.requestJob("Lisboa", "Lisboa", 10));
    }
	
	@Test
    public void testOriginInSouthRegion() throws Exception {
		assertEquals(_ID % 2 == 0, _port.requestJob("Faro", "Lisboa", 10) == null);
    }
	
	@Test
	public void testDestinationInNorthRegion() throws Exception {
		assertEquals(_ID % 2 != 0, _port.requestJob("Porto", "Lisboa", 10) == null);
	}
	
	@Test
    public void testDestinationInCentreRegion() throws Exception {
		assertNotNull(_port.requestJob("Lisboa", "Lisboa", 10));
    }
	
	@Test
    public void testDestinationInSouthRegion() throws Exception {
		assertEquals(_ID % 2 == 0, _port.requestJob("Faro", "Lisboa", 10) == null);
    }
	
    /**
     * The following tests test if the transporter throws 
     * BadLocationFault_Exception if the origin/destination is not valid
     */
    @Test
    public void testUnknownOrigin() throws Exception {
    	String badCityName = "not a city";
    	try {
			_port.requestJob(badCityName, "Lisboa", 10);
			fail();
		} catch (BadLocationFault_Exception e) {
			assertEquals("The origin city is invalid", e.getMessage());
			assertEquals(badCityName, e.getFaultInfo().getLocation());
		}
    }
    
    @Test
    public void testUnknownDestination() throws Exception {
    	String badCityName = "not a city";
    	try {
			_port.requestJob("Lisboa", badCityName, 10);
			fail();
		} catch (BadLocationFault_Exception e) {
			assertEquals("The destination city is invalid", e.getMessage());
			assertEquals(badCityName, e.getFaultInfo().getLocation());
		}
    }
    
    
    // Price Related Tests
    
    @Test
    public void testPriceLessOrEqualZero() throws Exception{
    	int badPrice = -10;
    	try {
			_port.requestJob("Lisboa", "Lisboa", badPrice);
			fail();
		} catch (BadPriceFault_Exception e) {
			assertEquals("The reference price is invalid", e.getMessage());
			assertEquals(badPrice, (int) e.getFaultInfo().getPrice());
		}
    }
    
    @Test
    public void testPriceGreaterThanOneHundred() throws Exception{
    		JobView job = _port.requestJob("Lisboa", "Lisboa", 101);
    		assertEquals(null, job);
    }
    
    @Test
    public void testPriceLessOrEqualTen() throws Exception{
		JobView job = _port.requestJob("Lisboa", "Lisboa", 9);
		assertTrue(job.getJobPrice() < 9);
    }
    
    @Test
    public void testOddPriceBetweenTenAndHundred() throws Exception{
    	int price = 15;
    	JobView job = _port.requestJob("Lisboa", "Lisboa", price);
    	assertTrue((_ID % 2 == 0) ? job.getJobPrice() > price : job.getJobPrice() < price);
    }
    
    @Test
    public void testEvenPriceBetweenTenAndHundred() throws Exception{
    	int price = 16;
    	JobView job = _port.requestJob("Lisboa", "Lisboa", price);
    	assertTrue((_ID % 2 == 0) ? job.getJobPrice() < price : job.getJobPrice() > price);
    }
}













