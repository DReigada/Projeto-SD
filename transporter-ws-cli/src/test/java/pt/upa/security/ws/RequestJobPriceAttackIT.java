package pt.upa.security.ws;

import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;

import pt.upa.transporter.ws.JobView;

public class RequestJobPriceAttackIT extends BaseIT {
	
    // We assume that clearJobs it's working, or else some tests will fail
    @After
    public void tearDown(){
    	transporter1.setIsTest(0);
    	super.tearDown();
    }

    @Test
    public void testRequestJobModifyPrice() throws Exception{
    	String  city1 = "Lisboa",
    			city2 = "Coimbra";
    	
    	transporter1.setIsTest(1);
    	JobView job = transporter1.requestJob(city1, city2, 50);
    	assertNull(job);
    }
    
    @Test
    public void testChangeSignature() throws Exception{
    	String  city1 = "Lisboa",
    			city2 = "Coimbra";

    	transporter1.setIsTest(2);
    	JobView job = transporter1.requestJob(city1, city2, 50);
    	assertNull(job);
    }   
    
    @Test
    public void testChangeCounter() throws Exception{
    	String  city1 = "Lisboa",
    			city2 = "Coimbra";

    	transporter1.setIsTest(3);
    	JobView job = transporter1.requestJob(city1, city2, 50);
    	assertNull(job);
    } 
} 
