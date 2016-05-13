package pt.upa.broker.backup.ws;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import mockit.Mocked;
import pt.upa.broker.ws.Broker;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;

public class BaseTest {
	
	public static final String COMPANY_1_NAME = "UpaTransporter1";
	public static final String COMPANY_2_NAME = "UpaTransporter2";
	public static final String ORIGIN_1 = "Lisboa";
	public static final String ORIGIN_2 = "Coimbra";
	public static final String DESTINATION_1 = "Castelo Branco";
	public static final String DESTINATION_2 = "Santarém";
	public static final String BAD_LOCATION = "not a city";
	public static final int PRICE_1 = 5;
	public static final int PRICE_2 = 8;
	public static final String ID_1 = "1";
	public static final String ID_2 = "2";
	public static final String INVALID_ID_1 = "invalid";
	public static final String INVALID_ID_2 = "0";
	public static final String INVALID_ID_3 = "3";
	public static final TransportStateView STATE_1 = TransportStateView.BOOKED;
	public static final TransportStateView STATE_2 = TransportStateView.FAILED;
	
	// the JobViews created to mock transporters responses
	protected static TransportView _transport1, _transport2, _updatedTransport1, _updatedTransport2;
	
	@BeforeClass
	public static void oneTimeSetUp(){
		_transport1 = new TransportView();
		_transport1.setTransporterCompany(COMPANY_1_NAME);
		_transport1.setOrigin(ORIGIN_1);
		_transport1.setDestination(DESTINATION_1);
		_transport1.setId(ID_1);
		_transport1.setPrice(PRICE_1);
		_transport1.setState(STATE_1);
				
		_transport2 = new TransportView();
		_transport2.setTransporterCompany(COMPANY_1_NAME);
		_transport2.setOrigin(ORIGIN_2);
		_transport2.setDestination(DESTINATION_2);
		_transport2.setId(ID_2);
		_transport2.setPrice(PRICE_2);
		_transport2.setState(STATE_2);
		
		_updatedTransport1 = new TransportView();
		_updatedTransport1.setTransporterCompany(COMPANY_1_NAME);
		_updatedTransport1.setOrigin(ORIGIN_1);
		_updatedTransport1.setDestination(DESTINATION_1);
		_updatedTransport1.setId(ID_1);
		_updatedTransport1.setPrice(PRICE_1);
		_updatedTransport1.setState(STATE_2);
		
		_updatedTransport2 = new TransportView();
		_updatedTransport2.setTransporterCompany(COMPANY_1_NAME);
		_updatedTransport2.setOrigin(ORIGIN_2);
		_updatedTransport2.setDestination(DESTINATION_2);
		_updatedTransport2.setId(ID_2);
		_updatedTransport2.setPrice(PRICE_1);
		_updatedTransport2.setState(STATE_1);
	}
	
	
	@AfterClass
	public static void oneTimeTearDown(){
		_transport1 = null;
		_transport2 = null;
	}
	
	
	protected BrokerBackupPort _backup;
	protected BrokerPort _port;
	@Mocked
	protected Broker _broker;
	
	
    @Before
    public void setUp() {
    	_backup = new BrokerBackupPort(_broker);
    	_port = _backup.getBrokerPort();
    }

    @After
    public void tearDown() {
    	_backup = null;
    }
}
