package pt.upa.transporter.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.CounterBackup;
import pt.upa.transporter.ws.cli.TransporterClient;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class BaseIT {
	private static class mockCounterBackup implements CounterBackup{
		@Override
		public void updateMessageCounter(int val) {	}
	}
	private static final String TEST_PROP_FILE = "/test.properties";
	private static Properties props = null;
	
	protected static TransporterClient transporter1 = null,
										transporter2 = null;
	

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
		props = new Properties();
		try {
			props.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
		String uddiURL = props.getProperty("uddi.url");
		String transporterBaseName = props.getProperty("ws.baseName");
		
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		String endpointAddress1 = uddiNaming.lookup(transporterBaseName + "1");
		String endpointAddress2 = uddiNaming.lookup(transporterBaseName + "2");	

		transporter1 = new TransporterClient(endpointAddress1, new mockCounterBackup());
		transporter2 = new TransporterClient(endpointAddress2, new mockCounterBackup());
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	transporter1 = null;
    	transporter2 = null;
    }
    
    @Before
    public void setUp() throws Exception{
    	
    }
    
    // We assume that clearJobs it's working, or else some tests will fail
    @After
    public void tearDown(){
    	transporter1.clearJobs();
    	transporter2.clearJobs();
    }
}