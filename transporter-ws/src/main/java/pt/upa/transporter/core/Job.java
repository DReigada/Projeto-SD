package pt.upa.transporter.core;

import pt.upa.transporter.ws.JobView;

/**
 * This class implements a Job from a {@link Transporter}
 *
 */
public class Job {
    protected String _transporterName;
    protected String _identifier;
    protected String _origin;
    protected String _destination;
    protected int _price;
    protected State _state;
    
    /**
     * The states of a job
     */
    public enum State{
        PROPOSED,
        REJECTED,
        ACCEPTED,
        HEADING,
        ONGOING,
        COMPLETED;
    }

    /**
     * The default constructor
     * @param transporterName  
     * @param identifier
     * @param origin
     * @param destination
     * @param price
     * @param state
     */
    public Job(String transporterName,
	    		String identifier,
	    		String origin,
	    		String destination, 
	    		int price,
	    		State state) {
		_transporterName = transporterName;
		_identifier = identifier;
		_origin = origin;
		_destination = destination;
		_price = price;
		_state = state;
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
	
	/**
	 * Changes the state of this job
	 * @param state the new state
	 */
	public void setState(State state){
		_state = state;
	}
	
	/**
	 * TODO implement this
	 * @return
	 */
	public JobView getView(){
		return null;
	}


    
}
