package pt.upa.transporter.core;

import java.util.Hashtable;

public class Transporter {
	private String _name;
	private int _ID;
	private Hashtable<String, Job> _jobsList;
	private int _jobCounter;
	private Regions _operatingRegions;
	
	public Transporter(int ID) {
		_ID = ID;
		_name = "UpaTransporter" + ID;
		_jobsList = new Hashtable<String, Job>();
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
	 * @throws InvalidCityException if origin or destination are not valid cities 
	 */
	public Job requestJob(String origin, String destination, int value) throws BadPriceException, InvalidCityException{
		// case the reference value is not valid
		if(value <= 0) throw new BadPriceException();
		// case the given cities are not valid
		if(!(Regions.validateCity(origin) && Regions.validateCity(destination))) throw new InvalidCityException();
		
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
		if (value % 2  != 0) return (_ID % 2  != 0) ? --value : ++value;
		return (_ID % 2  == 0) ? --value : ++value;
	}

}
