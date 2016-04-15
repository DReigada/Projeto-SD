package pt.upa.transporter.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.core.Transporter;

public class AuxMethodsTest {
	private TransporterPort _port = null;
	
	// Set up a transporter and request two jobs
    @Before
    public void setUp() throws Exception {
    	_port = new TransporterPort(new Transporter(1));
    }

    @After
    public void tearDown() {
    	_port = null;
    }
    
    @Test
    public void testListJobs() throws Exception{
    	assertTrue(_port.listJobs().isEmpty());
    
    	JobView[] jobArray = {
    			_port.requestJob("Lisboa", "Faro", 50),
    			_port.requestJob("Coimbra", "Faro", 50),
    			_port.requestJob("Faro", "Faro", 50)};
    	List<JobView> jobsReturned = _port.listJobs();
    	
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
    	_port.requestJob("Lisboa", "Faro", 50);
    	_port.requestJob("Coimbra", "Faro", 50);
    	_port.requestJob("Faro", "Faro", 50);
    	
    	_port.clearJobs();
    	
    	assertTrue(_port.listJobs().isEmpty());
    }
    
    @Test
    public void testPingReturn(){
    	String ping = "Ping";
    	assertEquals("Received message: " + ping ,_port.ping(ping));
    }
}
