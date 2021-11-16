import java.util.Random;
import java.util.concurrent.Semaphore;

public class Elf implements Runnable {

	//An enum type is a special data type that enables for a variable to be a set of predefined constants.
	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR
	};

	private ElfState state;
	/**
	 * The number associated with the Elf
	 */
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;
	//create a thread variable
	Thread mythread = null;

	//stop requesting
	private boolean stopThreadRequest = false;

	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
	}


	public ElfState getState() {
		return state;
	}



	/**
	 * Santa might call this function to fix the trouble
	 * @param state
	 */
	public void setState(ElfState state) {
		this.state = state;
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
		mythread = Thread.currentThread();
		if (!isStopThreadRequest()) {
			System.out.println("Print this text here. It works.");
		}
		while (!isStopThreadRequest()) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//TODO Auto-generated catch block
				System.out.println("Elf Thread is canceled");
				return;
				//e.printStackTrace();
			}
			switch (state) {
				case WORKING: {
					// at each day, there is a 1% chance that an elf runs into
					// trouble.
					if (rand.nextDouble() < 0.01) {
						state = ElfState.TROUBLE;
					}
					break;
				}
				case TROUBLE:
					// FIXME: if possible, move to Santa's door
				case AT_SANTAS_DOOR:
					// FIXME: if feasible, wake up Santa
					break;
			}
		}

	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}

	public Thread getThread() {
		return mythread;
	}
}
