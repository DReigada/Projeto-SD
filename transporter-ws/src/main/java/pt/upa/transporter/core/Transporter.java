package pt.upa.transporter.core;

import java.util.Hashtable;

public class Transporter {
	private String _name;
	private Hashtable<String, Job> _jobsList;
	
	public Transporter(String name) {
		_name = name;
		_jobsList = new Hashtable<String, Job>(); //TODO check the values for the hashtable
	}
	
	public Job requestJob(String origin, String destination, int price){
		
	}
	
	
	public String getName(){return _name;}

	
	
}
