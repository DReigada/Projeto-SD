package pt.upa.broker.backup.ws;

import java.util.Timer;

import javax.jws.WebService;

import pt.upa.broker.ws.Broker;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerTransportView;
import pt.upa.broker.ws.TransportView;
import pt.upa.transporter.ws.cli.CounterBackup;
import security.ws.handler.SignatureHandler;

@WebService(endpointInterface = "pt.upa.broker.backup.ws.BrokerBackup")
public class BrokerBackupPort implements BrokerBackup, CounterBackup{
	
	
	private BrokerPort _port;
	private boolean _firstPing;
	private Broker _broker;
	private BrokerFailureDetector _failureDetector; 
	private Timer _failureTimer;
	
	public BrokerBackupPort(Broker broker){
		_port = new BrokerPort();
		_firstPing = true;
		_broker = broker;
	}
	
	@Override
	public void updateTransport(TransportView transport, String transporterID) {
		BrokerTransportView view = new BrokerTransportView(transport, transporterID);
		_port.updateTransport(view);
	}

	@Override
	public void clearTransports() {
		_port.clearBackup();
	}

	@Override
	public void updateMessageCounter(int val) {
		SignatureHandler.counter = val;
	}
	
	@Override
	public void ping() {
		if(_firstPing){
			_firstPing = false;
			
			_failureDetector = new BrokerFailureDetector(this, System.currentTimeMillis());
			_failureTimer = new Timer(true);	// create a daemon timer
			
			_failureTimer.schedule(_failureDetector, 0, Broker.TIME_CHECK_BETWEEN_PINGS);		
		}
		else{
			_failureDetector.updateTime(System.currentTimeMillis());
		}
	}

	
	void makePrimary(){
		_failureTimer.cancel();
		_broker.makePrimary(_port);
	}

	public BrokerPort getBrokerPort() {
		return _port;
	}

	
	
}
