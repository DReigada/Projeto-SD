package pt.upa.transporter.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.upa.transporter.core.Transporter;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class RequestJobTest {

    // static members
	private static TransporterPort _port= null;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	_port = new TransporterPort(new Transporter(1));
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_port = null;
    }


    // members
    

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void testUnknownOrigin() throws Exception {
    	try {
			_port.requestJob("not a city", "Lisboa", 10);
			fail();
		} catch (BadLocationFault_Exception e) {
			assertEquals("The origin city is invalid", e.getMessage());
			assertEquals("not a city", e.getFaultInfo().location);
		}
    }

}