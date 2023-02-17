import lejos.hardware.Button;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {

	public static void main(String[] args) {
		
		//Initializing behaviors
		
		
		//Initializing threads
		ExitThread CheckExit = new ExitThread();
				
		//waiting to start program 
		Arbitrator arbitrator = new Arbitrator(new Behavior[] {});

		//start program
		Button.LEDPattern(1); //steady green light

		//starting threads
		CheckExit.start();

		//start movement
		arbitrator.go();

	}

}
