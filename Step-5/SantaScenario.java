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
	public int numberOfWaitingReindeer;
	Semaphore waitelf = new Semaphore(0, true);
	Semaphore trouble = new Semaphore(1, true);
	Semaphore door = new Semaphore(1, true);
	Semaphore waitReindeer = new Semaphore(0, true);


	public boolean isDecember;


	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		//create a semaphore



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
		scenario.numberOfWaitingReindeer = 0;
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
			} catch (InterruptedException e) {
				System.out.println("Main Threads get Canceled");
				return;
				//e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}

			//after day 370 terminate threads using deferred termination NO interrupt() or throw new InterruptedException()

			if (day > 370) {
				scenario.santa.requestStop();
				for (int i = 0; i != 10; i++) {
					scenario.elves.get(i).requestStop();
				}
				for (Reindeer reindeer : scenario.reindeers) {
					reindeer.requestStop();
				}
			}

			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			scenario.santa.report();
			for (Elf elf : scenario.elves)
				elf.report();
			try {
				scenario.trouble.acquire();
				int size = scenario.inTrouble.size();
				scenario.trouble.release();
				//check if the elf in queue are in trouble state and at least more than 2
				if (scenario.inTrouble.size() > 2  && day < 370) {
					//writing this will check if the queue at the door is empty.
					scenario.door.acquire();
					if (scenario.atDoor.isEmpty()) {
						scenario.trouble.acquire();
						for (int i = 0; i < size; i++) {
							//this will get the elf with the trouble
							Elf elf = scenario.inTrouble.remove();
							elf.setState(Elf.ElfState.AT_SANTAS_DOOR);
							scenario.atDoor.add(elf);
							scenario.waitelf.release();
						}
						scenario.trouble.release();
					}
					scenario.door.release();
				}
			} catch(InterruptedException e) {
				scenario.trouble.release();
				scenario.door.release();
				return;
			}

			for (Reindeer reindeer : scenario.reindeers) {
				reindeer.report();
			}
			}
		}
}
