package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;
import mockit.Verifications;

public class RequestTransportTest extends BaseTest{

    @Test
    public void testRequestOneTransport() throws Exception{
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobView1;
    	}};
    	
    	String id = _broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	assertEquals("0", id);
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testRequestTwoTransports() throws Exception{
    	
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobView1;
    	}};
    	
    	String id1 = _broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	String id2 = _broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);

    	assertEquals("0", id1);
    	assertEquals("1", id2);
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 2;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 2;
    	}};
    }
    
    @Test
    public void testRequestTransportWithInvalidLocation() throws Exception{
    	UnknownLocationFault fault = new UnknownLocationFault();
    	fault.setLocation(BAD_LOCATION);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(BAD_LOCATION, DESTINATION_1, PRICE_1);
    		result = new UnknownLocationFault_Exception(ERROR_MESSAGE_1, fault);
    	}};
    	
    	try {
			_broker.requestTransport(BAD_LOCATION, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnknownLocationFault_Exception e) {
			assertEquals(ERROR_MESSAGE_1, e.getMessage());
			assertEquals(BAD_LOCATION, e.getFaultInfo().getLocation());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(BAD_LOCATION, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }

}








