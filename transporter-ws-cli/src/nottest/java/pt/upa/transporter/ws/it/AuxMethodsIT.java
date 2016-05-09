package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import pt.upa.transporter.ws.JobView;

public class AuxMethodsIT extends BaseIT{
	
   @Test
    public void testListJobs() throws Exception{
    	assertTrue(transporter1.listJobs().isEmpty());
    
    	JobView[] jobArray = {
    			transporter1.requestJob("Lisboa", "Faro", 50),
    			transporter1.requestJob("Coimbra", "Faro", 50),
    			transporter1.requestJob("Faro", "Faro", 50)};
    	List<JobView> jobsReturned = transporter1.listJobs();
    	
    	assertTrue(jobsReturned.size() == 3);
    	
    	for (int i = 0; i < jobArray.length; i++) {
    		JobView job = jobArray[i];
    		JobView jobReturned = jobsReturned.get(i);
    		
        	assertEquals(job.getJobOrigin(), jobReturned.getJobOrigin());
        	assertEquals(job.getJobDestination(), jobReturned.getJobDestination());
        	assertEquals(job.getCompanyName(), jobReturned.getCompanyName());
        	assertEquals(job.getJobIdentifier(), jobReturned.getJobIdentifier());
        	assertEquals(job.getJobPrice(), jobReturned.getJobPrice());
        	assertEquals(job.getJobState(), jobReturned.getJobState());
		}
    }
    
   @Test
    public void testClearJobs() throws Exception{
    	transporter1.requestJob("Lisboa", "Faro", 50);
    	transporter1.requestJob("Coimbra", "Faro", 50);
    	transporter1.requestJob("Faro", "Faro", 50);
    	
    	transporter1.clearJobs();
    	
    	assertTrue(transporter1.listJobs().isEmpty());
    }
    
   @Test
    public void testPingReturn(){
    	String ping = "Ping";
    	assertEquals("Received message: " + ping , transporter1.ping(ping));
    }
}
