//import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER};
	private SantaState state;

	//added this
	Thread mythread = null;

	//stop requesting
	private boolean stopThreadRequest = false;


	public Santa(SantaScenario scenario) {
		this.state = SantaState.SLEEPING;
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
		mythread = Thread.currentThread();
		if (!isStopThreadRequest()) {
			System.out.println("Print this text here. It works.");
		}
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

	public Thread getThread() {
		return mythread;
	}


}
