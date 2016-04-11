package pt.upa.transporter.simulator;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import pt.upa.transporter.core.Job;
import pt.upa.transporter.core.Job.State;

/**
 * A class that implements the Evolution of a job state, with random intervals [1, 5] seconds
 */
public class JobStateEvolution implements Runnable {
	private ScheduledThreadPoolExecutor _timer;	// the Thread Pool that executes this Runnable
	private Job _job;
	private ThreadLocalRandom _random;
	
	/**
	 * The default Constructor
	 * @param job the job to simulate the state
	 * @param timer the ScheduledThreadPoolExecutor that will run this Runnable
	 */
	public JobStateEvolution(Job job, ScheduledThreadPoolExecutor timer) {
		_job = job;
		_timer = timer;
		_random = ThreadLocalRandom.current();
		
		_timer.schedule(this, _random.nextInt(1000, 5000), TimeUnit.MILLISECONDS);	// adds this task to the timer
	}
	
	@Override
	public void run() {
		switch (_job.getState()) {
			case ACCEPTED:
				_job.setState(State.HEADING);
				_timer.schedule(this, _random.nextInt(1000, 5000), TimeUnit.MILLISECONDS); // add this runnable to the pool again
				break;
			case HEADING:
				_job.setState(State.ONGOING);
				_timer.schedule(this, _random.nextInt(1000, 5000), TimeUnit.MILLISECONDS); // add this runnable to the pool once again
				break;
			case ONGOING:
				_job.setState(State.COMPLETED);
				break;
			default:
				break;
		}
	}

}
