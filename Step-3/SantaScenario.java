import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;


public class SantaScenario {

	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	//added new variables
	public Queue<Elf> atDoor;
	public Queue<Elf> inTrouble;


	public boolean isDecember;


	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		//create a semaphore
		Semaphore sema = new Semaphore(1);

		scenario.isDecember = false;
		// create the participants
		// Santa
		scenario.santa = new Santa(scenario);
		Thread th = new Thread(scenario.santa);
		th.start();
		// The elves: in this case: 10
		scenario.elves = new ArrayList<>();

		//storing the elves at the door and that they are in trouble. Elves are in queue. FIFO.
		scenario.atDoor = new LinkedList<>();
		scenario.inTrouble = new LinkedList<>();

		for (int i = 0; i != 10; i++) {
			Elf elf = new Elf(i + 1, scenario);
			scenario.elves.add(elf);
			th = new Thread(elf);
			th.start();
		}
		// The reindeer: in this case: 9
		scenario.reindeers = new ArrayList<>();
		for (int i = 0; i != 9; i++) {
			Reindeer reindeer = new Reindeer(i + 1, scenario);
			scenario.reindeers.add(reindeer);
			th = new Thread(reindeer);
			th.start();
		}

		// now, start the passing of time

		for (int day = 1; day < 500; day++) {
			// wait a day
			try {
				Thread.sleep(100);
			}catch (InterruptedException e) {
				System.out.println("Main Threads get Canceled");
				return;
				//e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}

			//day 370 terminate threads using deferred termination NO interrupt() or InterruptedException
			if (day == 370) {
				System.out.println("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Main Thread day is still counting<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-----------");
				System.out.println("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>The Santa Thread day is terminated<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-----------");
				System.out.println("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>The Elf Thread day is terminated<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-----------");
				System.out.println("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>The Reindeer Thread day is terminated<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-----------");
				scenario.santa.requestStop();

				for (Elf elf : scenario.elves) {
					elf.requestStop();
				}
				for (Reindeer reindeer: scenario.reindeers) {
					reindeer.requestStop();
				}
			}

			if (day > 370) {
				System.out.println("********************* Reindeer are gone after day 370 ****************************");
				scenario.reindeers.clear();
			}

			// print out the state:
				System.out.println("***********  Day " + day + " *************************");
				scenario.santa.report();
				for (Elf elf : scenario.elves)
					elf.report();
				if (day < 370) {
//					if (!elf.getThread().isAlive()) {
//						System.out.println("elf thread is terminated");
//					}
					for (Reindeer reindeer : scenario.reindeers)
						reindeer.report();
						//	if (!reindeer.getThread().isAlive()) {
//						System.out.println("reindeer thread is terminated");
//					}

				}
		}
	}
}
