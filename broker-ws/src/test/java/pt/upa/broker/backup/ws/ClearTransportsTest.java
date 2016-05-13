package pt.upa.broker.backup.ws;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClearTransportsTest extends BaseTest{

	@Test
	public void clearTransportsTest(){
		assertEquals(0, _port._transports.size());
		
		_backup.updateTransport(_transport1, ID_1);
		
		assertEquals(1, _port._transports.size());
		
		_backup.clearTransports();
		
		assertEquals(0, _port._transports.size());
	}
}
