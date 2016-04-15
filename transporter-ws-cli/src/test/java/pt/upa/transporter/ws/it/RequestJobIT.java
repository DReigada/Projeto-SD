package pt.upa.transporter.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

public class RequestJobIT extends BaseIT{

    // Test the returned JobView
    @Test
    public void testReturnedJobView() throws Exception{
    	String 	city1 = "Lisboa",
    			city2 = "Coimbra";
    	
    	JobView job = transporter1.requestJob(city1, city2, 50);
    	assertEquals(city1, job.getJobOrigin());
    	assertEquals(city2, job.getJobDestination());
    	assertEquals("UpaTransporter1", job.getCompanyName());
    	assertEquals("1", job.getJobIdentifier());
    	assertTrue(job.getJobPrice() > 0);
    	assertEquals(JobStateView.PROPOSED, job.getJobState());
    	
    	assertNull(transporter1.requestJob(city1, city2, 500));
    	
    	job = transporter2.requestJob(city2, city1, 10);
    	assertEquals(city2, job.getJobOrigin());
    	assertEquals(city1, job.getJobDestination());
    	assertEquals("UpaTransporter2", job.getCompanyName());
    	assertEquals("1", job.getJobIdentifier());
    	assertTrue(job.getJobPrice() > 0);
    	assertEquals(JobStateView.PROPOSED, job.getJobState());
    }
    
    /**
     * The following tests test if the transporter throws 
     * BadLocationFault_Exception if the origin/destination is not valid
     */
    @Test
    public void testUnknownOrigin() throws Exception {
    	String badCityName = "not a city";
    	try {
			transporter1.requestJob(badCityName, "Lisboa", 10);
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
			transporter1.requestJob("Lisboa", badCityName, 10);
			fail();
		} catch (BadLocationFault_Exception e) {
			assertEquals("The destination city is invalid", e.getMessage());
			assertEquals(badCityName, e.getFaultInfo().getLocation());
		}
    }
    
    @Test
    public void testPriceEqualZero() throws Exception{
    	int badPrice = 0;
    	try {
			transporter1.requestJob("Lisboa", "Lisboa", badPrice);
			fail();
		} catch (BadPriceFault_Exception e) {
			assertEquals("The reference price is invalid", e.getMessage());
			assertEquals(badPrice, (int) e.getFaultInfo().getPrice());
		}
    }
}

