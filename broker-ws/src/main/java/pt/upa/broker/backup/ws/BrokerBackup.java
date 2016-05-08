package pt.upa.broker.backup.ws;

import javax.jws.Oneway;
import javax.jws.WebService;

import pt.upa.broker.ws.BrokerTransportView;

@WebService
public interface BrokerBackup {
	
	public void addTransport(BrokerTransportView transport);
	
	public void updateMessageCounter(int val);
	
	@Oneway public void Ping();
}
