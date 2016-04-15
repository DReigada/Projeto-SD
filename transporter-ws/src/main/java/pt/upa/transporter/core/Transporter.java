package pt.upa.transporter.core;

import java.util.Collection;
import java.util.HashMap;

import pt.upa.transporter.core.Exceptions.BadLocationException;
import pt.upa.transporter.core.Exceptions.BadPriceException;

public class Transporter {
	private String _name;
	private int _ID;
	private HashMap<String, Job> _jobsList;
	private int _jobCounter;
	private Regions _operatingRegions;
	
	public Transporter(int ID) {
		_ID = ID;
		_name = "UpaTransporter" + ID;
		_jobsList = new HashMap<String, Job>();
		_jobCounter = 0;
		_operatingRegions = new Regions(ID%2==0, true, ID%2!=0);
	}

	/**
	 * 
	 * @param origin the origin city
	 * @param destination the destination city
	 * @param value the reference value
	 * @return the created job or null if the job was not accepted
	 * @throws BadPriceException if value is lesser or equal to 0
	 * @throws BadLocationException if origin or destination are not valid cities 
	 */
	public Job requestJob(String origin, String destination, int value) throws BadPriceException, BadLocationException{
		// case the reference value is not valid
		if(value <= 0) throw new BadPriceException("The reference price is invalid");
		// case the given cities are not valid
		if(origin == null || !Regions.validateCity(origin)) throw new BadLocationException("The origin city is invalid", origin);
		if(destination == null ||!Regions.validateCity(destination)) throw new BadLocationException("The destination city is invalid", destination);
		
		// case this transporter does not operate in the given cities
		if(!(_operatingRegions.hasCity(origin) && _operatingRegions.hasCity(destination))) return null;
		
		// else generate a price and create a new job if interested
		int price = generatePrice(value);
		if(price == -1) return null; // not interested 
		
		Job newJob = new Job(_name, Integer.toString(++_jobCounter), origin, 
								destination, price, Job.State.PROPOSED);
		// add the job to the jobs list
		_jobsList.put(newJob.getIdentifier(), newJob);
		
		return newJob;
	}
	
	/**
	 * Returns the job with the given ID
	 * @param id the ID to search for
	 * @return Returns the job with the given ID (null if no Job was found)
	 */
	public Job getJobById(String id){
		return _jobsList.get(id);
	}
	
	/**
	 * @return all the Jobs from this Transporter
	 */
	public Collection<Job> getAllJobs(){
		return _jobsList.values();
	}
	
	/**
	 * Removes all Jobs from this transporter and resets the jobs ID
	 */
	public void deleteAllJobs(){
		_jobsList.clear();
		_jobCounter = 0;
	}
	
	/**
	 * Returns this Transporter's name
	 * @return this Transporter's name
	 */
	public String getName(){return _name;}
	
	
	/** Private Implementation **/
	
	/**
	 * Generates a Price from the given a reference value (it assumes that value is > 0)
	 * @param value the reference value
	 * @return the price generated (-1 if not interested)
	 */
	private int generatePrice(int value) {
		if (value > 100) return -1;
		if (value <= 10) return --value;
		return (value % 2  == _ID % 2) ? --value : ++value;
	}

}
