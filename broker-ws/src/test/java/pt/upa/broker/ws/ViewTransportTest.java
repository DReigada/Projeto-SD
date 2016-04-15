package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;
import mockit.Verifications;
import pt.upa.transporter.ws.BadLocationFault_Exception;

public class ViewTransportTest extends BaseTest {
	
	/* TEST INVALID IDS */
	
	// TEST: invalid id non number
	@Test
    public void testViewTransportWithInvalidIdOne() throws Exception{
    	UnknownTransportFault fault = new UnknownTransportFault();
    	fault.setId(INVALID_ID_1);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	
    	try {
			_broker.viewTransport(INVALID_ID_1);
			fail();
		} catch (UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.", e.getMessage());
			assertEquals(INVALID_ID_1, e.getFaultInfo().getId());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	
	
	// TEST: invalid id number 0
	@Test
    public void testViewTransportWithInvalidIdTwo() throws Exception{
    	UnknownTransportFault fault = new UnknownTransportFault();
    	fault.setId(INVALID_ID_2);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	
    	try {
			_broker.viewTransport(INVALID_ID_2);
			fail();
		} catch (UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.", e.getMessage());
			assertEquals(INVALID_ID_2, e.getFaultInfo().getId());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	
	// TEST: invalid id number > _transports.size()
	@Test
    public void testViewTransportWithInvalidIdThree() throws Exception{
    	UnknownTransportFault fault = new UnknownTransportFault();
    	fault.setId(INVALID_ID_3);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	
    	try {
			_broker.viewTransport(INVALID_ID_3);
			fail();
		} catch (UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.", e.getMessage());
			assertEquals(INVALID_ID_3, e.getFaultInfo().getId());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	// TEST: invalid id null
	@Test
    public void testViewTransportWithNullId() throws Exception{
    	UnknownTransportFault fault = new UnknownTransportFault();
    	fault.setId(null);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	
    	try {
			_broker.viewTransport(null);
			fail();
		} catch (UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.", e.getMessage());
			assertEquals(null, e.getFaultInfo().getId());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	
	/* TESTS VALID ID */
	// Only one element in _transports that is a valid transport
	@Test
    public void testViewTransportWithOneElement() throws Exception{
    	UnknownTransportFault fault = new UnknownTransportFault();
    	fault.setId(null);
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	
    	try {
			_broker.viewTransport(null);
			fail();
		} catch (UnknownTransportFault_Exception e) {
			assertEquals("No transports match the given transport identifier.", e.getMessage());
			assertEquals(null, e.getFaultInfo().getId());
		}
    	
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	// Only one element in _transports that is an invalid transport
	// TEST: two valid transports, check one of them
	// TEST: two invalid transports, check one of them
	// TEST: one invalid one valid, test the invalid one
	// TEST: one invalid one valid, test the valid one
	// TEST if manager returns null - company stopped business
	
	// TEST: all transport states
}
