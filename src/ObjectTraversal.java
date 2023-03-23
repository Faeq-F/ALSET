import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ObjectTraversal implements Behavior{
	
	//How close an obstruction should be from the robot before the object traversal behavior is executed.
	private final static float distanceFromObject = 0.126f;
	//delay to prevent further code from running too early in ObjectTraversal
	private final static int delayOT = 1700;
	//storing how far the motors have turned
	private static int tach_start, tach_end;
	
	@Override
	public boolean takeControl() {
		return Main.getBTconnection().isConnectedToPhone()
			&& (Main.getUSval().getDistanceFromObject() < distanceFromObject)
			&& !(Main.getTouch().isPaused());
	}
	
	@Override
	public void action() {
		
		LCD.clear();
		LCD.drawString("Traversing Object", 0, 3);
		
		movement.stop();
		//turn to try and go around the obstacle
		movement.turnLeft(0, 0);
		movement.stop();
		// rotate the UltraSonic sensor so that it is pointing towards the obstacle
		movement.USRight();
		movement.stop();
		
		//to keep moving forward until the obstacle is no longer detected
		tach_start = movement.getTachoCount();
		movement.forward();
		while(true)
			if (Main.getUSval().getDistanceFromObject() >= distanceFromObject)
				break;
		Delay.msDelay(delayOT);
		
		//once the obstacle is no longer detected
		movement.stop();
		tach_end = movement.getTachoCount();
		// (the UltraSonic sensor is still facing the obstacle)
		movement.turnRight(0, 0);
		
		movement.forward();
		Delay.msDelay(delayOT);
		// keep moving forward until side of obstacle can no longer be detected
		while(true)
			if (Main.getUSval().getDistanceFromObject() >= distanceFromObject)
				break;
		Delay.msDelay(delayOT + 700); // 700; ensure the phone can pass by as well 
		
		movement.stop();
		movement.turnRight(0, 0);
		// go forward the number of revolutions the motors
		//initially turned to get back to the position of the track
		movement.forward((int) tach_end - tach_start);
		movement.stop();
		
		// rotate the UltraSonic sensor back to its original position
		// we should be in front of the obstacle now, on top of the track
		movement.USLeft();
		movement.stop();
		movement.turnLeft(0,0);
	}

	/**
	 * Does nothing to the behavior. 
	 * This behavior should never be suppressed
	 * (should be allowed to complete it's action() once it has taken control)
	 */
	@Override
	public void suppress() {}

}
