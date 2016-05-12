package pt.upa.broker.backup.ws;

import pt.upa.transporter.ws.cli.CounterBackup;

public class CounterBackupAdapter implements CounterBackup{
	
	private BrokerBackup _backupPort;
	
	public CounterBackupAdapter(BrokerBackup backupPort) {
		_backupPort = backupPort;
	}
	
	@Override
	public void updateMessageCounter(int val) {
		if(_backupPort == null) return;
			_backupPort.updateMessageCounter(val);
	}

}
