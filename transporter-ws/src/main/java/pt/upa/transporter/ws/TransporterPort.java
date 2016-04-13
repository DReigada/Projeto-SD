package pt.upa.transporter.ws;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;

import pt.upa.transporter.core.Job;
import pt.upa.transporter.core.Transporter;
import pt.upa.transporter.core.Exceptions.BadLocationException;
import pt.upa.transporter.core.Exceptions.BadPriceException;
import pt.upa.transporter.simulator.JobStateSimulator;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType"
	)
public class TransporterPort implements TransporterPortType{
	
	Transporter _transporter;
	public JobStateSimulator _jobSimulator;
	
	public TransporterPort(Transporter transporter) {
		_transporter = transporter;
		_jobSimulator = new JobStateSimulator();
	}
	
	/**
	 * @param name 
	 * @return a diagnosis message
	 */
	@Override
	public String ping(String name) {
		return "Received message: " + name;
	}
	
	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		try{
			Job newJob = _transporter.requestJob(origin, destination, price);
			if(newJob == null) return null;
			return newJob.getView();
		}
		catch(BadPriceException e){
			BadPriceFault fault = new BadPriceFault();
			fault.price = price;
			throw new BadPriceFault_Exception(e.getMessage(), fault);
		}
		catch (BadLocationException e) {
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(e.getLocation());
			throw new BadLocationFault_Exception(e.getMessage(), fault);
		}
	}
	
	/**
	 * Changes the state of the job based if it was accepted or not 
	 * @param id the id of the job
	 * @param accept true if the job was accepeted
	 * @return the job that was changed (null if no job was found it the given ID)
	 * @throws BadJobFault_Exception in case the ID is invalid
	 */
	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		Job job = _transporter.getJobById(id);
		if (job == null){
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception("Invalid Job ID", fault);
		}
		job.setState(accept ? Job.State.ACCEPTED : Job.State.REJECTED);
		if(accept) _jobSimulator.addJob(job);
		return job.getView();
	}
	
	/**
	 * Gets the job based with the given ID 
	 * @param id the id of the job
	 * @return the job (null if no job was found it the given ID)
	 */
	@Override
	public JobView jobStatus(String id) {
		Job job = _transporter.getJobById(id);
		if(job == null) return null;
		return job.getView();
	}

	@Override
	public List<JobView> listJobs() {
	  Collection<Job> jobs = _transporter.getAllJobs();
	  List<JobView> views = jobs.stream().map(Job -> Job.getView()).collect(Collectors.toList());

	  return views;
	}

	@Override
	public void clearJobs() {
		_transporter.deleteAllJobs();
	}
	
	/**
	 * Stops the job simulator
	 */
	void stopSimulator(){
		_jobSimulator.stop();
	}
}
