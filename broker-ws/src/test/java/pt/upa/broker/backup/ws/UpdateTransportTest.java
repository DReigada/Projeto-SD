package pt.upa.broker.backup.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pt.upa.broker.ws.BrokerTransportView;
import pt.upa.broker.ws.TransportView;

public class UpdateTransportTest extends BaseTest{
	
    @Test
    public void addOneTransportTest() throws Exception{
    	BrokerTransportView transport;

    	_backup.updateTransport(_transport1, ID_1);
    	try{
    		transport = _port._transports.get(0);
    	} catch(IndexOutOfBoundsException e){
    		fail();
    		return;
    	}
    	
    	compareTransportView(_transport1, transport.getTransportView());;
    }
    
    @Test
    public void addTwoTransportTest() throws Exception{
    	BrokerTransportView transport1, transport2;

    	_backup.updateTransport(_transport1, ID_1);
    	_backup.updateTransport(_transport2, ID_2);
    	
    	try{
    		transport1 = _port._transports.get(0);
    		transport2 = _port._transports.get(1);
    	} catch(IndexOutOfBoundsException e){
    		fail();
    		return;
    	}
    	
    	compareTransportView(_transport1, transport1.getTransportView());
    	compareTransportView(_transport2, transport2.getTransportView());
    }
    
    
    @Test
    public void updateOneTransportTest() throws Exception{
    	BrokerTransportView transport;

    	_backup.updateTransport(_transport1, ID_1);
    	_backup.updateTransport(_updatedTransport1, ID_1);
    	
    	try{
    		transport = _port._transports.get(0);
    	} catch(IndexOutOfBoundsException e){
    		fail();
    		return;
    	}
    	
    	compareTransportView(_updatedTransport1, transport.getTransportView());;
    }
    
    @Test
    public void updateTwoTransportTest() throws Exception{
    	BrokerTransportView transport1, transport2;

    	_backup.updateTransport(_transport1, ID_1);
    	_backup.updateTransport(_updatedTransport1, ID_1);
    	_backup.updateTransport(_transport2, ID_2);
    	_backup.updateTransport(_updatedTransport2, ID_2);
    	
    	try{
    		transport1 = _port._transports.get(0);
    		transport2 = _port._transports.get(1);
    	} catch(IndexOutOfBoundsException e){
    		fail();
    		return;
    	}
    	
    	compareTransportView(_updatedTransport1, transport1.getTransportView());
    	compareTransportView(_updatedTransport2, transport2.getTransportView());
    }

    
    
    private void compareTransportView(TransportView v1, TransportView v2){
    	assertEquals(v1.getDestination(), v2.getDestination());
    	assertEquals(v1.getId(), v2.getId());
    	assertEquals(v1.getOrigin() , v2.getOrigin());
    	assertEquals(v1.getPrice(), v2.getPrice());
    	assertEquals(v1.getState() , v2.getState());
    	assertEquals(v1.getTransporterCompany() , v2.getTransporterCompany());
    }
}
