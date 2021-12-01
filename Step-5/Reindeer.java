import java.util.Random;
import java.util.concurrent.Semaphore;

public class Reindeer implements Runnable {

	public enum ReindeerState {AT_BEACH, AT_WARMING_SHED, AT_THE_SLEIGH, Terminated};
	private ReindeerState state;
	private SantaScenario scenario;

	//stop requesting
	private boolean stopThreadRequest = false;

	private Random rand = new Random();

	/**
	 * The number associated with the reindeer
	 */
	private int number;

	public Reindeer(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ReindeerState.AT_BEACH;
	}

	public synchronized  void requestStop() {
		this.stopThreadRequest = true;
	}
	public synchronized boolean isStopThreadRequest() {
		return this.stopThreadRequest;
	}

	@Override
	public void run() {
		//added a variable that copy the current thread
		while(!isStopThreadRequest()) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Reindeer Thread is canceled");
				return;
				//e.printStackTrace();
			}
			// see what we need to do:
			switch(state) {
				case AT_BEACH: { // if it is December, the reindeer might think about returning from the beach
					if (scenario.isDecember) {
						if (rand.nextDouble() < 0.1) {
							state = ReindeerState.AT_WARMING_SHED;
							scenario.numberOfWaitingReindeer++;
						}
					}
					break;
				}
				case AT_WARMING_SHED:
					// if all the reindeer are home, wake up santa
					try {
						if(scenario.numberOfWaitingReindeer > 8) {
							scenario.santa.WakeUp(false);
						}
						scenario.waitReindeer.acquire();
					}catch (InterruptedException e) {
						scenario.waitReindeer.release();
						state = ReindeerState.Terminated;
						return;
					}
					break;
				case AT_THE_SLEIGH:
					// keep pulling
					break;
			}
		}
	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Reindeer " + number + " : " + state);
	}
	public void setReindeerstate () {
		state = ReindeerState.AT_THE_SLEIGH;
	}
}
