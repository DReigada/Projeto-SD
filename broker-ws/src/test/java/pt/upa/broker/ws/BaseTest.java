package pt.upa.broker.ws;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import mockit.Mocked;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class BaseTest {

	// Static values to be used for comparing values
	public static final String COMPANY_1_NAME = "UpaTransporter1";
	public static final String COMPANY_2_NAME = "UpaTransporter2";
	public static final String ORIGIN_1 = "Lisboa";
	public static final String ORIGIN_2 = "Coimbra";
	public static final String DESTINATION_1 = "Castelo Branco";
	public static final String DESTINATION_2 = "Santarém";
	public static final String BAD_LOCATION = "not a city";
	public static final int PRICE_1 = 5;
	public static final int PRICE_2 = 8;
	public static final int PRICE_3 = 10;
	public static final int BAD_PRICE = -7;
	public static final String ID_1 = "1";
	public static final String ID_2 = "2";
	public static final String INVALID_ID_1 = "invalid";
	public static final String INVALID_ID_2 = "0";
	public static final String INVALID_ID_3 = "3";
	public static final JobStateView STATE_1 = JobStateView.PROPOSED;
	public static final JobStateView STATE_2 = JobStateView.ACCEPTED;
	public static final JobStateView STATE_COMPLETED = JobStateView.COMPLETED;
	public static final String ERROR_MESSAGE_1= "error message";
	
	// the JobViews created to mock transporters responses
	protected static JobView _jobView1, _jobView2, _jobViewTransporter2, _jobViewCompleted, _jobViewAccepted;
	
	// initialize the jobViews
	@BeforeClass
	public static void oneTimeSetUp(){
		_jobView1 = new JobView();
		_jobView1.setCompanyName(COMPANY_1_NAME);
		_jobView1.setJobOrigin(ORIGIN_1);
		_jobView1.setJobDestination(DESTINATION_1);
		_jobView1.setJobIdentifier(ID_1);
		_jobView1.setJobPrice(PRICE_1);
		_jobView1.setJobState(STATE_1);
		
		_jobView2 = new JobView();
		_jobView2.setCompanyName(COMPANY_1_NAME);
		_jobView2.setJobOrigin(ORIGIN_2);
		_jobView2.setJobDestination(DESTINATION_2);
		_jobView2.setJobIdentifier(ID_2);
		_jobView2.setJobPrice(PRICE_2);
		_jobView2.setJobState(STATE_2);
		
		_jobViewAccepted = new JobView();
		_jobViewAccepted.setCompanyName(COMPANY_1_NAME);
		_jobViewAccepted.setJobOrigin(ORIGIN_1);
		_jobViewAccepted.setJobDestination(DESTINATION_1);
		_jobViewAccepted.setJobIdentifier(ID_1);
		_jobViewAccepted.setJobPrice(PRICE_1);
		_jobViewAccepted.setJobState(STATE_2);
		
		_jobViewCompleted = new JobView();
		_jobViewCompleted.setCompanyName(COMPANY_1_NAME);
		_jobViewCompleted.setJobOrigin(ORIGIN_1);
		_jobViewCompleted.setJobDestination(DESTINATION_1);
		_jobViewCompleted.setJobIdentifier(ID_1);
		_jobViewCompleted.setJobPrice(PRICE_1);
		_jobViewCompleted.setJobState(STATE_COMPLETED);
		
		_jobViewTransporter2 = new JobView();
		_jobViewTransporter2.setCompanyName(COMPANY_2_NAME);
		_jobViewTransporter2.setJobOrigin(ORIGIN_1);
		_jobViewTransporter2.setJobDestination(DESTINATION_1);
		_jobViewTransporter2.setJobIdentifier(ID_1);
		_jobViewTransporter2.setJobPrice(PRICE_2);
		_jobViewTransporter2.setJobState(STATE_1);
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		_jobView1 = null;
	}
	
	protected BrokerPort _broker;

	@Mocked
	protected TransporterClient transporter;
	@Mocked
	protected TransporterClient transporter2;
	@Mocked 
	protected TransporterCompaniesManager manager;
	

    @Before
    public void setUp() {
    	_broker = new BrokerPort();
    }

    @After
    public void tearDown() {
    	_broker = null;
    }
}
