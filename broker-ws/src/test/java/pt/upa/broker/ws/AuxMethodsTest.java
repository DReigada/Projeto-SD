package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import mockit.Expectations;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

public class AuxMethodsTest extends BaseTest {
	
	private static final String PING_MESSAGE = "Ping message";
	
	@Test
	public void testPing() throws Exception{
		new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter, transporter2);
    		transporter.ping(PING_MESSAGE); result = "Received: " + PING_MESSAGE;
    		transporter2.ping(PING_MESSAGE); result = "Received: " + PING_MESSAGE;
		}};
		
		String pingReturn =  PING_MESSAGE + ": Connected successfully to " + 2
			      + " of " + 2 + " transporter companies.";
		assertEquals(pingReturn, _broker.ping(PING_MESSAGE));
	}
	
	@Test
	public void testListAndClearTransports() throws Exception{
		
		JobView _jobView1A = new JobView();
		_jobView1A.setCompanyName(COMPANY_1_NAME);
		_jobView1A.setJobOrigin(ORIGIN_1);
		_jobView1A.setJobDestination(DESTINATION_1);
		_jobView1A.setJobIdentifier(ID_1);
		_jobView1A.setJobPrice(PRICE_1);
		_jobView1A.setJobState(JobStateView.ACCEPTED);
		
		JobView _jobView2A = new JobView();
		_jobView2A.setCompanyName(COMPANY_1_NAME);
		_jobView2A.setJobOrigin(ORIGIN_2);
		_jobView2A.setJobDestination(DESTINATION_2);
		_jobView2A.setJobIdentifier(ID_2);
		_jobView2A.setJobPrice(PRICE_2);
		_jobView2A.setJobState(JobStateView.ACCEPTED);
		
    	new Expectations() {{
    		manager.getAllTransporterPorts(); result = Arrays.asList(transporter);
    		manager.getTransporterPort(COMPANY_1_NAME); result = transporter;
    		transporter.requestJob(ORIGIN_1, DESTINATION_1, PRICE_1); result = _jobView1;
    		transporter.requestJob(ORIGIN_2, DESTINATION_2, PRICE_2); result = _jobView2;
    		transporter.jobStatus(ID_1); result = _jobView1A;
    		transporter.jobStatus(ID_2); result = _jobView2A;
    		
    	}};
    	
    	List<TransportView> jobs = _broker.listTransports();
    	
		assertEquals(0, jobs.size());
		
		String id1 =_broker.requestTransport(ORIGIN_1, DESTINATION_1, PRICE_1);
		String id2 = _broker.requestTransport(ORIGIN_2, DESTINATION_2, PRICE_2);
		jobs = _broker.listTransports();
		
		assertEquals(2, jobs.size());
		
		TransportView job1 = jobs.get(0);
		TransportView job2 = jobs.get(1);
		
		assertEquals(id1, job1.getId());
		assertEquals(COMPANY_1_NAME , job1.getTransporterCompany());
		assertEquals(ORIGIN_1 , job1.getOrigin());
		assertEquals(DESTINATION_1, job1.getDestination());
		assertEquals(PRICE_1 , (int) job1.getPrice());
		assertEquals(TransportStateView.BOOKED, job1.getState());
		
		assertEquals(id2, job2.getId());
		assertEquals(COMPANY_1_NAME , job2.getTransporterCompany());
		assertEquals(ORIGIN_2 , job2.getOrigin());
		assertEquals(DESTINATION_2, job2.getDestination());
		assertEquals(PRICE_2 , (int) job2.getPrice());
		assertEquals(TransportStateView.BOOKED, job2.getState());
		
		
		_broker.clearTransports();
		jobs = _broker.listTransports();
		assertEquals(0, jobs.size());
	}
	
}
