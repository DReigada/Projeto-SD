package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;
import mockit.Verifications;

public class RequestViewTransortTest extends BaseTest {
	
	// TEST: test with two transporters
	@Test
    public void testWithTwoTransporters() throws Exception{
    	
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
	
	// TEST: best proposed price = reference price
	@Test
    public void testEdgeCasePrice() throws Exception{
    	
    	new Expectations(){{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter, transporter2);
    		transporter.requestJob(anyString, anyString, anyInt);
    		result = _jobView1;
    		transporter2.requestJob(anyString, anyString, anyInt);
    		result = _jobViewTransporter2;
    		
    		manager.getTransporterPort(anyString); result = transporter;
    		transporter.jobStatus(anyString); result = _jobViewAccepted;
    	}};
    	
    	_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
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
    		transporter2.requestJob(anyString, anyString, anyInt); times = 1;
    	}};
    }

	

}
