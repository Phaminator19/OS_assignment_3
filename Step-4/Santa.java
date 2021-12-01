//import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER, Terminated};
	private SantaState state;
	private SantaScenario scenario;
	//stop requesting
	private boolean stopThreadRequest = false;


	public Santa(SantaScenario scenario) {
		this.state = SantaState.SLEEPING;
		this.scenario = scenario;
	}

	public void setState(SantaState state) {
		this.state = state;
	}

	public synchronized void requestStop() {
		this.stopThreadRequest = true;
	}
	public synchronized boolean isStopThreadRequest() {
		return this.stopThreadRequest;
	}


	@Override
	public void run() {
		//added a variable that copy the current thread
		while(!isStopThreadRequest()) {
			// wait a day...
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block\
				System.out.println("Santa Thread is canceled");
				return;
				//e.printStackTrace();
			}
			switch(state) {
				case SLEEPING: // if sleeping, continue to sleep
					break;
				case WOKEN_UP_BY_ELVES:
					// FIXME: help the elves who are at the door and go back to sleep
					try {
						this.scenario.door.acquire();
						int size = this.scenario.atDoor.size();
						for (int i = 0; i < size; i++) {
							this.scenario.atDoor.remove().setState(Elf.ElfState.WORKING);
						}
						this.scenario.door.release();

					} catch(InterruptedException e) {
						this.scenario.door.release();
						setState(SantaState.Terminated);
						return;
					}
					state = SantaState.SLEEPING;
					break;
				case WOKEN_UP_BY_REINDEER:
					// FIXME: assemble the reindeer to the sleigh then change state to ready
					break;
				case READY_FOR_CHRISTMAS: // nothing more to be done
					break;
			}
		}


	}


	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Santa : " + state);
	}

	//this method will change the state of Santa when there is a problem from the Elves or the Reindeer.
	public void WakeUp(boolean isAwake) {
		if (this.state == SantaState.SLEEPING) {
			if (isAwake) {
				setState(SantaState.WOKEN_UP_BY_ELVES);
			}
			else {
				setState(SantaState.WOKEN_UP_BY_REINDEER);
			}
		}
	}


}
