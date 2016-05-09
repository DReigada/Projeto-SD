package pt.upa.broker.backup.ws;

import javax.jws.Oneway;
import javax.jws.WebParam;
import javax.jws.WebService;

import pt.upa.broker.ws.TransportView;

@WebService
public interface BrokerBackupBase {
	
	public void updateTransport(@WebParam(name = "transport") TransportView transport, @WebParam(name = "transporterID") String transporterID);
	
	public void clearTransports(); 
	
	public void updateMessageCounter(@WebParam(name = "val") int val);
	
	@Oneway public void ping();
}
