package pt.upa.transporter.ws;

import static org.junit.Assert.assertEquals;
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
    
	private TransporterPort _port= null;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	_port = new TransporterPort(new Transporter(_ID));
    }

    @After
    public void tearDown() {
    	_port = null;
    }


    // tests

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













