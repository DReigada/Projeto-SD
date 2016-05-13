package pt.upa.security.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.transporter.ws.JobView;

public class RequestJobPriceAttackIT extends BaseIT {
	
	@Before
	public void setUp() throws Exception{
    	transporter1.setIsTest(1);
	}
	
    // We assume that clearJobs it's working, or else some tests will fail
    @After
    public void tearDown(){
    	super.tearDown();
    	transporter1.setIsTest(0);
    }

    @Test
    public void testRequestJobModifyPrice() throws Exception{
    	String  city1 = "Lisboa",
    			city2 = "Coimbra";

    	JobView job = transporter1.requestJob(city1, city2, 50);
    	assertNull(job);
    }
    
} 
