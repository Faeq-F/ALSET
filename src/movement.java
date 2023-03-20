import lejos.utility.Delay;

public class movement {
	
	/**
	 * rotates the UltraSonic sensor ~90 degrees to the left  
	 */
	public static void USLeft() {
		Main.mUltraSonic.rotate(-100);
	}
	
	/**
	 * rotates the UltraSonic sensor ~90 degrees to the right  
	 */
	public static void USRight() {
		Main.mUltraSonic.rotate(100);
	}
	
	/**
	 * moves the robot forward  
	 */
	public static void forward() {
		Main.mL.startSynchronization();
		Main.mL.forward();
		Main.mR.forward();
		Main.mL.endSynchronization();
	}
	
	/**
	 * moves the robot forward
	 * @param degrees to rotate motor by
	 */
	public static void forward(int degrees) {
		Main.mL.startSynchronization();
		Main.mL.rotate(degrees, true);
		Main.mR.rotate(degrees, true);
		Main.mL.endSynchronization();
		Main.mL.waitComplete();
	}
	
	/**
	 * moves the robot backward
	 * @param ms how long to move the robot backwards for
	 */
	public static void backward(long ms) {
		Main.mL.startSynchronization();
		Main.mL.backward();
		Main.mR.backward();
		Main.mL.endSynchronization();
		Delay.msDelay(ms);
	}
	
	/**
	 * rotates the robot ~90 degrees to the left
	 * @param backwards how long to move the robot backwards for, before the turn (ms)
	 * @param forward number of degrees to move the motors attached to the wheel by (forward), after the turn
	 */
	public static void turnLeft(long backwards, int forward) {
		if (backwards != 0) backward(backwards);
		stop();
		Main.mL.startSynchronization();
		Main.mL.rotate(Main.ROT90DEGREES, true);
		Main.mR.rotate(-Main.ROT90DEGREES, true);
		Main.mL.endSynchronization();
		Main.mL.waitComplete();
		if (forward != 0) forward(forward);
	}
	
	/**
	 * rotates the robot ~90 degrees to the right
	 * @param backwards how long to move the robot backwards for, before the turn (ms)
	 * @param forward number of degrees to move the motors attached to the wheel by (forward), after the turn
	 */
	public static void turnRight(long backwards, int forward) {
		if (backwards != 0) backward(backwards);
		stop();
		Main.mL.startSynchronization();
		Main.mL.rotate(-Main.ROT90DEGREES, true);
		Main.mR.rotate(Main.ROT90DEGREES, true);
		Main.mL.endSynchronization() ;
		Main.mL.waitComplete();
		if (forward != 0) forward(forward);
	}
	
	/**
	 * Stops the wheels from turning
	 */
	public static void stop() {
		Main.mL.startSynchronization();
		Main.mL.stop(true);
		Main.mR.stop(true);
		Main.mL.endSynchronization();
	}
	
	/**
	 * sets the speed of the motors connected to the wheels
	 * @param speed (degrees per second)
	 */
	public static void setSpeed(int speed) {
		Main.mL.setSpeed(speed);
		Main.mR.setSpeed(speed);
	}
	
	/**returns the rotational speed of the wheels (rpm)
	 * @return the tachometer count
	 */
	public static int getTachoCount() {
		return Main.mL.getTachoCount();
	}
}