
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ObjectTraversal implements Behavior{	
	
	@Override
	public boolean takeControl() {
		return (Main.connectedToPhone && Main.getDistanceFromObject() < WALLFROMROBOT);
	}

	/**
	 * How close should an obstruction be from the robot before this 
	 * class is executed.
	 */
	final static float WALLFROMROBOT = 0.126f ;
	
	/**
	 * Value used for a delay so to allow robot to move forwards for a whike
	 * before running any further code.
	 */
	final static int DELAYBY = 1000 ;
	
	
	@Override
	public void action() {
		// ULTRASONIC SENSOR PARTS
		// assign tacho to vars
		int tach_start, tach_end ;
		
		// turn robot to the left.
		movement.turnLeft() ;
		
		// Also turn distance detection so that it points
		// towards wall.
		movement.SensRight() ;
		
		// 		KEEP GOING UNTIL WALL IS NO LONGER DETECTED
		// Get starting tacho count, this will be used later
		tach_start = movement.getTachoCount() ;
		
		// keep going until no more obstruction detected
		movement.forward() ;
		
		float current_distance ;
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLFROMROBOT) {
				break;
			}
		}
		
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
		
		// assign tacho count to a var
		tach_end = movement.getTachoCount() ;
		
		// stop robot
		movement.stop() ;
		
		// turn -90, do NOT change distance sensor position
		movement.turnRight() ;
				
		// keep going forwards for a little bit before starting to 
		// detect wall as when robot turns, sensor is a bit behind
		// and don't want robot to turn prematurely.
		movement.forward() ;
		Delay.msDelay(DELAYBY) ;
		
		// keep going until wall is not detected on the side
		// as obstruction may be a box.
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLFROMROBOT) {
				break;
			}	
		}
		
		// keep going forwards for a little bit then stop
		// otherwise if detecting space instantly it will not detect
		// anything as the obstruction is a bit ahead of the robot.
		Delay.msDelay(DELAYBY) ;
				
		// stop robot
		movement.stop() ;
		
		// turn right again
		movement.turnRight() ;
		
		// go forward the amount of tacho count robot travelled
		// after turning left but before it turn right.
		float distance_travelled = tach_end - tach_start ;
		movement.forward((int) distance_travelled) ;
		
		// then rotate 90 and rotate UltraSonic sensor back to its original
		// direction. In theory, we should be back where robot 
		// was when it was behind the box/obstruction.
		movement.SensLeft() ;
		movement.turnLeft() ;
	}
	
	
	/**
	 * Used for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectTraversal ot = new ObjectTraversal() ;
		movement.initializeAll() ;
		ExitThread checkExit = new ExitThread() ;
		ObjectDetection OD = new ObjectDetection() ;
		
		
		OD.start() ;
		checkExit.start() ;
		ot.action() ;
		
		OD.stop_thread = true ;
		
		
//		System.exit(0) ;
		
		
	}

	@Override
	public void suppress() {
		
	}
	

}
