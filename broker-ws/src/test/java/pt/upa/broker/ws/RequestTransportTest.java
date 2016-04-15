package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;
import mockit.Verifications;
import pt.upa.transporter.ws.BadLocationFault;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public class RequestTransportTest extends BaseTest{

    @Test
    public void testRequestOneTransport() throws Exception{
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobView1;
    	}};
    	
    	String id = _broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	assertEquals("1", id);
    	
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

    	assertEquals("1", id1);
    	assertEquals("2", id2);
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 2;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 2;
    	}};
    }
    
    @Test
    public void testRequestOnTwoTransporters() throws Exception{
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter, transporter2);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobView1;
    		transporter2.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobViewTransporter2;
    	}};
    	
    	String id = _broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1); 	
    	assertEquals("1", id);
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    		transporter2.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testRequestTransportWithInvalidLocation() throws Exception{
    	BadLocationFault fault = new BadLocationFault();
    	fault.setLocation(BAD_LOCATION);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(BAD_LOCATION, DESTINATION_1, PRICE_1);
    		result = new BadLocationFault_Exception(ERROR_MESSAGE_1, fault);
    	}};
    	
    	try {
			_broker.requestTransport(BAD_LOCATION, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnknownLocationFault_Exception e) {
			assertEquals("Unknown origin or destination.", e.getMessage());
			assertEquals(BAD_LOCATION, e.getFaultInfo().getLocation());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(BAD_LOCATION, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testRequestTransportWithNullLocation() throws Exception{
    	BadLocationFault fault = new BadLocationFault();
    	fault.setLocation(null);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(null, DESTINATION_1, PRICE_1);
    		result = new BadLocationFault_Exception(ERROR_MESSAGE_1, fault);
    	}};
    	
    	try {
			_broker.requestTransport(null, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnknownLocationFault_Exception e) {
			assertEquals("Unknown origin or destination.", e.getMessage());
			assertEquals(null, e.getFaultInfo().getLocation());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(null, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testRequestTransportWithInvalidPrice() throws Exception{
    	BadPriceFault fault = new BadPriceFault();
    	fault.setPrice(BAD_PRICE);
    	    	
    	try {
			_broker.requestTransport(ORIGIN_1, DESTINATION_1, BAD_PRICE);
			fail();
		} catch (InvalidPriceFault_Exception e) {
			assertEquals("Invalid price.", e.getMessage());
			assertEquals(BAD_PRICE, (int) e.getFaultInfo().getPrice());
		}
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 0;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, BAD_PRICE); times = 0;
    	}};
    }
    
    @Test
    public void testCatchBadPriceFault() throws Exception{
    	BadPriceFault fault = new BadPriceFault();
    	fault.setPrice(PRICE_1);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1);
    		result = new BadPriceFault_Exception(ERROR_MESSAGE_1, fault);
    	}};
    	
    	try {
			_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
			fail();
		} catch (InvalidPriceFault_Exception e) {
			assertEquals("Invalid price.", e.getMessage());
			assertEquals(PRICE_1, (int) e.getFaultInfo().getPrice());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testUnavailableTransportPriceFault_Exception() throws Exception{
		
    	JobView job = new JobView();
		job.setCompanyName(COMPANY_1_NAME);
		job.setJobOrigin(ORIGIN_1);
		job.setJobDestination(DESTINATION_1);
		job.setJobIdentifier(ID_1);
		job.setJobPrice(PRICE_2);		// higher price than requested
		job.setJobState(STATE_1);
		
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = job;
    	}};
    	
    	try {
			_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnavailableTransportPriceFault_Exception e) {
			assertEquals("No transport available for the requested price.", e.getMessage());
			assertEquals(PRICE_2, (int) e.getFaultInfo().getBestPriceFound());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
    @Test
    public void testNoTransporterActive() throws Exception{
		
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = null;
    	}};
    	
    	try {
			_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnavailableTransportFault_Exception e) {
			assertEquals("No transporter companies available.", e.getMessage());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 0;
    	}};
    }
    
    @Test
    public void testNoInterestedTransporter() throws Exception{
		
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = null;
    	}};
    	
    	try {
			_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
			fail();
		} catch (UnavailableTransportFault_Exception e) {
			assertEquals("No transport available for the requested route.", e.getMessage());
			assertEquals(ORIGIN_1, e.getFaultInfo().getOrigin());
			assertEquals(DESTINATION_1, e.getFaultInfo().getDestination());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); times = 1;
    	}};
    }
    
}








