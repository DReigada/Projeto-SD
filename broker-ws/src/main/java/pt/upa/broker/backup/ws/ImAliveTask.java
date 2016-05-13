package pt.upa.broker.backup.ws;

import java.util.TimerTask;

public class ImAliveTask extends TimerTask{

	private BrokerBackup _port;
	
	public ImAliveTask(BrokerBackup port) {
		_port = port;
	}
	
	@Override
	public void run() {
		_port.ping();		
	}
	
}
