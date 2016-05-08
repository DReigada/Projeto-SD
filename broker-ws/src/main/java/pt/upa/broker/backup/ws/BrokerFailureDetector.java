package pt.upa.broker.backup.ws;

import java.util.TimerTask;

public class BrokerFailureDetector extends TimerTask{
	
	private long _timeSinceLastPing;
	private BrokerBackupPort _port;
	
	public BrokerFailureDetector(BrokerBackupPort port, long firstPing) {
		_port = port;
		_timeSinceLastPing = firstPing;
	}
	
	@Override
	public void run() {
		if (System.currentTimeMillis() - BrokerBackupPort.TIME_CHECK_BETWEEN_PINGS > _timeSinceLastPing) {
			_port.makePrimary();
		}
	}
	
	public void updateTime(long time){
		_timeSinceLastPing = time;
	}
}
