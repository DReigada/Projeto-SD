package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.ws.JobView;

public class JobStatusIT extends BaseIT{
	
	JobView _job1, _job2, _job3;
	
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	_job1 = transporter1.requestJob("Lisboa", "Coimbra", 50);
    	_job2 = transporter1.requestJob("Coimbra", "Faro", 50);
    	_job3 = transporter2.requestJob("Viseu", "Aveiro", 80);
    }
    
    @After
    public void tearDown(){
    	super.tearDown();
    	_job1 = null;
    	_job2 = null;
    	_job3 = null;
    }
    
    @Test
    public void testWithValidID(){
    	JobView job1 = transporter1.jobStatus(_job1.getJobIdentifier());
    	JobView job2 = transporter1.jobStatus(_job2.getJobIdentifier());
    	JobView job3 = transporter2.jobStatus(_job3.getJobIdentifier());
    	
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

    	assertEquals(_job3.getJobOrigin(), job3.getJobOrigin());
    	assertEquals(_job3.getJobDestination(), job3.getJobDestination());
    	assertEquals(_job3.getCompanyName(), job3.getCompanyName());
    	assertEquals(_job3.getJobIdentifier(), job3.getJobIdentifier());
    	assertEquals(_job3.getJobPrice(), job3.getJobPrice());
    	assertEquals(_job3.getJobState(), job3.getJobState());
    }
	
    @Test
    public void testWithInvalidID(){
    	assertNull(transporter1.jobStatus("not valid ID"));
    }
}
