package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;
import mockit.Verifications;
import pt.upa.transporter.ws.JobView;

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
	// TEST: only one element in _transports (valid)
	@Test
    public void testViewTransportWithOneValidTransport() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    		manager.getTransporterPort(anyString); result = transporter;
    		transporter.jobStatus(anyString); result = _jobViewAccepted;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_2);
    	TransportView transport1 = _broker.viewTransport(ID_1);
		
    	assertEquals(ID_1, transport1.getId());
		assertEquals(ORIGIN_1, transport1.getOrigin());
		assertEquals(DESTINATION_1, transport1.getDestination());
		assertTrue(PRICE_1 == transport1.getPrice());
		assertEquals(COMPANY_1_NAME, transport1.getTransporterCompany());
    	
		
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    		manager.getTransporterPort(anyString); times = 1;
    		transporter.jobStatus(anyString); times = 1;
    	}};
    }
	
	// TEST: only one element in _transports (invalid)
	@Test
    public void testViewTransportWithOneInvalidTransport() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView2;
    	}};
    	
    	try{
    		_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
    	} catch (Exception e){}
    	
    	TransportView transport1 = _broker.viewTransport(ID_1);
		
    	assertEquals(ID_1, transport1.getId());
		assertEquals(ORIGIN_1, transport1.getOrigin());
		assertEquals(DESTINATION_1, transport1.getDestination());
		assertTrue(null == transport1.getPrice());
		assertEquals(null, transport1.getTransporterCompany());
		assertEquals(TransportStateView.FAILED, transport1.getState());
    	
		
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }
	
	// TEST: only one element in _transports (valid) with transport price = reference price
	
	// TEST: test with two transporters (TODO: MOVE to another class)
	@Test
    public void testViewTransportWithTwoTransporters() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter, transporter2);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    		transporter2.requestJob(anyString, anyString, anyInt);
    		result = _jobViewTransporter2;
    		
    		manager.getTransporterPort(anyString); result = transporter;
    		transporter.jobStatus(anyString); result = _jobViewAccepted;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_3);
    	TransportView transport1 = _broker.viewTransport(ID_1);
		
    	assertEquals(ID_1, transport1.getId());
		assertEquals(ORIGIN_1, transport1.getOrigin());
		assertEquals(DESTINATION_1, transport1.getDestination());
		assertTrue(PRICE_1 == transport1.getPrice());
		assertEquals(COMPANY_1_NAME, transport1.getTransporterCompany());
    	
		
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 1;
    		transporter.requestJob(anyString, anyString, anyInt); times = 1;
    		transporter2.requestJob(anyString, anyString, anyInt); times = 1;
    		manager.getTransporterPort(anyString); times = 1;
    		transporter.jobStatus(anyString); times = 1;
    	}};
    }
	
	// TEST: two valid transports, check one
	@Test
    public void testViewTransportWithTwoValidTransports() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView2;
    		
    		manager.getTransporterPort(anyString); result = transporter;
    		transporter.jobStatus(anyString); result = _jobViewAccepted;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_3);
    	_broker.requestTransport(ORIGIN_2, DESTINATION_2, PRICE_3);
    	TransportView transport1 = _broker.viewTransport(ID_2);
		
    	assertEquals(ID_2, transport1.getId());
		assertEquals(ORIGIN_2, transport1.getOrigin());
		assertEquals(DESTINATION_2, transport1.getDestination());
		assertTrue(PRICE_2 == transport1.getPrice());
		assertEquals(COMPANY_1_NAME, transport1.getTransporterCompany());
		
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 2;
    		transporter.requestJob(anyString, anyString, anyInt); times = 2;
    		manager.getTransporterPort(anyString); times = 1;
    		transporter.jobStatus(anyString); times = 1;
    	}};
    }

	// TEST: two invalid transports, check one of them
	// TEST: one invalid one valid, test the invalid one
	// TEST: one invalid one valid, test the valid one
	
	// TEST if manager returns null - company stopped business
	@Test
    public void testViewTransportAfterCompanyLeft() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    		
    		manager.getTransporterPort(anyString); result = null;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_3);
    	TransportView transport1 = _broker.viewTransport(ID_2);
		
    	assertEquals(ID_1, transport1.getId());
		assertEquals(ORIGIN_1, transport1.getOrigin());
		assertEquals(DESTINATION_1, transport1.getDestination());
		assertTrue(PRICE_1 == transport1.getPrice());
		assertEquals(COMPANY_1_NAME, transport1.getTransporterCompany());
		assertEquals(TransportStateView.COMPLETED, transport1.getState());
		
    	new Verifications() {{
    		manager.getAllTransporterPorts(); times = 2;
    		transporter.requestJob(anyString, anyString, anyInt); times = 2;
    	}};
    }
	
	// TEST: all transport states
}
