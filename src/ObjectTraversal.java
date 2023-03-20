import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ObjectTraversal implements Behavior{
	
	@Override
	public boolean takeControl() {
		System.out.println(Main.getDistanceFromObject());
		return Main.connectedToPhone && (Main.getDistanceFromObject() < Main.distanceFromObject);
		//return false;
	}
	
	@Override
	public void action() {
		movement.turnLeft(0, 0);
		// rotate the UltraSonic sensor so that it is pointing towards the obstacle
		movement.USRight();
		
		//to keep moving forward until the obstacle is no longer detected
		Main.tach_start = movement.getTachoCount();
		movement.forward();
		while(true)
			if (Main.getDistanceFromObject() >= Main.distanceFromObject)
				break;
		Delay.msDelay(Main.delayOT);
		Main.tach_end = movement.getTachoCount();
		
		//once the obstacle is no longer detected
		movement.stop();
		//(the UltraSonic sensor is still facing the obstacle)
		movement.turnRight(0, 0);
		movement.forward();
		Delay.msDelay(Main.delayOT);
		
		// keep moving forward until side of obstacle can no longer be detected
		while(true)
			if (Main.getDistanceFromObject() >= Main.distanceFromObject)
				break;
		Delay.msDelay(Main.delayOT) ;
		movement.stop() ;
		
		movement.turnRight(0, 0) ;
		// go forward the number of revolutions the motors initially turned to get back to the position of the track
		movement.forward((int) Main.tach_end - Main.tach_start);
		
		// rotate the UltraSonic sensor back to its original position
		// we should be in front of the obstacle now, on top of the track
		movement.USLeft() ;
		movement.turnLeft(0, 0) ;
	}
	
	
	/**
	 * Used for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectTraversal ot = new ObjectTraversal() ;
		//movement.initializeAll() ;
		ExitThread checkExit = new ExitThread() ;
		ObjectDetection OD = new ObjectDetection() ;
		OD.start() ;
		checkExit.start() ;
		ot.action() ;
		//OD.stop_thread = true ;		
	}

	/**
	 * Does nothing to the behavior. 
	 * This behavior should never be suppressed
	 * (should be allowed to complete it's action() once it has taken control)
	 */
	@Override
	public void suppress() {}

}
