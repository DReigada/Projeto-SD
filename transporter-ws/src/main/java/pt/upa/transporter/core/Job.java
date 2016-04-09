package pt.upa.transporter.core;

public class Job {
    protected String _transporterName;
    protected String _identifier;
    protected String _origin;
    protected String _destination;
    protected int _price;
    protected State _state;
    
    
    public enum State{
        PROPOSED,
        REJECTED,
        ACCEPTED,
        HEADING,
        ONGOING,
        COMPLETED;
    }


    public Job(String transporterName,
	    		String identifier,
	    		String origin,
	    		String destination, 
	    		int price,
	    		State state) {
		this._transporterName = transporterName;
		this._identifier = identifier;
		this._origin = origin;
		this._destination = destination;
		this._price = price;
		this._state = state;
	}


	/**
	 * @return the transporterName
	 */
	public String getTransporterName() {
		return _transporterName;
	}


	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return _identifier;
	}


	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return _origin;
	}


	/**
	 * @return the destination
	 */
	public String getDestination() {
		return _destination;
	}


	/**
	 * @return the price
	 */
	public int getPrice() {
		return _price;
	}


	/**
	 * @return the State
	 */
	public State getState() {
		return _state;
	}


    
}
