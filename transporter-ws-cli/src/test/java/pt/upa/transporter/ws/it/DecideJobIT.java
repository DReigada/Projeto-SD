package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

public class DecideJobIT extends BaseIT {
	
	JobView _job1;
	
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	_job1 = transporter1.requestJob("Lisboa", "Coimbra", 50);
    }
    
    @After
    public void tearDown(){
    	super.tearDown();
    	_job1 = null;
    }
    
	// Test if BadJobFault_Exception is thrown if the ID is invalid
    @Test
    public void testInvalidJobID(){
    	String invalidID = "not a valid ID";
    	try {
			transporter1.decideJob(invalidID, true);
			fail();
		} catch (BadJobFault_Exception e) {
			assertEquals("Invalid Job ID", e.getMessage());
			assertEquals(invalidID, e.getFaultInfo().getId());
		}
    }
    
    // Test if the returned jobView matches the one return by requestJob
    // and that the state was changed to accepted
    @Test
    public void testAcceptJob() throws Exception{
    	JobView job1 = transporter1.decideJob(_job1.getJobIdentifier(), true);
    	
    	assertEquals(_job1.getJobOrigin(), job1.getJobOrigin());
    	assertEquals(_job1.getJobDestination(), job1.getJobDestination());
    	assertEquals(_job1.getCompanyName(), job1.getCompanyName());
    	assertEquals(_job1.getJobIdentifier(), job1.getJobIdentifier());
    	assertEquals(_job1.getJobPrice(), job1.getJobPrice());
    	assertEquals(JobStateView.ACCEPTED, job1.getJobState());
    }
    
    // Test if the returned jobView matches the one return by requestJob
    // and that the state was changed to rejected
    @Test
    public void testRejectJob() throws Exception{
    	JobView job1 = transporter1.decideJob(_job1.getJobIdentifier(), false);
    	
    	assertEquals(_job1.getJobOrigin(), job1.getJobOrigin());
    	assertEquals(_job1.getJobDestination(), job1.getJobDestination());
    	assertEquals(_job1.getCompanyName(), job1.getCompanyName());
    	assertEquals(_job1.getJobIdentifier(), job1.getJobIdentifier());
    	assertEquals(_job1.getJobPrice(), job1.getJobPrice());
    	assertEquals(JobStateView.REJECTED, job1.getJobState());
    }
    
}
