package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import mockit.Expectations;

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
}
