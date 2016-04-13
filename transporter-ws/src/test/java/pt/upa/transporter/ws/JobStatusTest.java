package pt.upa.transporter.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.core.Transporter;

public class JobStatusTest {
	
	private TransporterPort _port = null;
	private JobView _job1 = null,
					_job2 = null;
	
	
	// Set up a transporter and request two jobs
    @Before
    public void setUp() throws Exception {
    	_port = new TransporterPort(new Transporter(1));
    	_job1 = _port.requestJob("Lisboa", "Coimbra", 50);
    	_job2 = _port.requestJob("Viseu", "Aveiro", 80);
    }

    @After
    public void tearDown() {
    	_port = null;
    	_job1 = null;
    	_job2 = null;
    }
    
    // Test if the returned jobView match the ones return by requestJob
    @Test
    public void testWithExintingID(){
    	JobView job1 = _port.jobStatus("1");
    	JobView job2 = _port.jobStatus("2");
    	
    	assertEquals(_job1.getJobOrigin(), job1.getJobOrigin());
    	assertEquals(_job1.getJobDestination(), job1.getJobDestination());
    	assertEquals(_job1.getCompanyName(), job1.getCompanyName());
    	assertEquals(_job1.getJobIdentifier(), job1.getJobIdentifier());
    	assertEquals(_job1.getJobPrice(), job1.getJobPrice());
    	assertEquals(_job1.getJobState(), job1.getJobState());

    	assertEquals(_job2.getJobOrigin(), job2.getJobOrigin());
    	assertEquals(_job2.getJobDestination(), job2.getJobDestination());
    	assertEquals(_job2.getCompanyName(), job2.getCompanyName());
    	assertEquals(_job2.getJobIdentifier(), job2.getJobIdentifier());
    	assertEquals(_job2.getJobPrice(), job2.getJobPrice());
    	assertEquals(_job2.getJobState(), job2.getJobState());
    }
    
    // Test if the return value is null when the ID is invalid
    @Test
    public void testWithInvalidID(){
    	assertNull(_port.jobStatus("not valid ID"));
    }
}
