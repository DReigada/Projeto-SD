package pt.upa.broker.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class BrokerPort {
	
	@WebMethod
	public String ping(String pingOut) {
		return "Alive 'n kicking";
	}
	

	@WebMethod
	public void clearTransports(){
		
	}
}
