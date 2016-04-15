package pt.upa.transporter.simulator;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import pt.upa.transporter.core.Job;

/**
 * This class uses {@link JobStateEvolution} to simulate the evolution of a group of {@link Job}s
 */
public class JobStateSimulator{
	
	private ScheduledThreadPoolExecutor _timer;
	
	public JobStateSimulator(){
		_timer = new ScheduledThreadPoolExecutor(1);
	}
	
	/**
	 * Add a job to the simulation
	 * @param job the job to add
	 */
	public void addJob(Job job){
		new JobStateEvolution(job, _timer);
	}
	
	/**
	 * Stops the simulation.
	 * Must be called in order to kill all used Threads
	 */
	public void stop(){
		_timer.shutdown();
	}
}
