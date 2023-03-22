import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class movement {
	
	private final static Port UltraSonicMotorPort = MotorPort.C;
	public final static Port LeftMotorPort = MotorPort.A;
	public final static Port RightMotorPort = MotorPort.D;
	public final static int speed = 200; //decided upon as an appropriate speed
	//how much the motor needs to rotate by (degrees) to make the robot turn ~90 degrees
	public final static int ROT90DEGREES = 227;
	
	// motors for the robot
	public static BaseRegulatedMotor mL;
	public static BaseRegulatedMotor mR;
	public static BaseRegulatedMotor mUltraSonic;
	
	public static void initialiseAll() {
		mUltraSonic = new EV3MediumRegulatedMotor(UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		setSpeed(speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
	}
	
	/**
	 * rotates the UltraSonic sensor ~90 degrees to the left  
	 */
	public static void USLeft() {
		mUltraSonic.rotate(-100);
	}
	
	/**
	 * rotates the UltraSonic sensor ~90 degrees to the right  
	 */
	public static void USRight() {
		mUltraSonic.rotate(100);
	}
	
	/**
	 * moves the robot forward  
	 */
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	/**
	 * moves the robot forward
	 * @param degrees to rotate motor by
	 */
	public static void forward(int degrees) {
		mL.startSynchronization();
		mL.rotate(degrees, true);
		mR.rotate(degrees, true);
		mL.endSynchronization();
		mL.waitComplete();
	}
	
	/**
	 * moves the robot backward
	 * @param ms how long to move the robot backwards for
	 */
	public static void backward(long ms) {
		mL.startSynchronization();
		mL.backward();
		mR.backward();
		mL.endSynchronization();
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
		mL.startSynchronization();
		mL.rotate(ROT90DEGREES, true);
		mR.rotate(-ROT90DEGREES, true);
		mL.endSynchronization();
		mL.waitComplete();
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
		mL.startSynchronization();
		mL.rotate(-ROT90DEGREES, true);
		mR.rotate(ROT90DEGREES, true);
		mL.endSynchronization() ;
		mL.waitComplete();
		if (forward != 0) forward(forward);
	}
	
	/**
	 * Stops the wheels from turning
	 */
	public static void stop() {
		mL.startSynchronization();
		mL.stop(true);
		mR.stop(true);
		mL.endSynchronization();
	}
	
	/**
	 * sets the speed of the motors connected to the wheels
	 * @param speed (degrees per second)
	 */
	public static void setSpeed(int speed) {
		mL.setSpeed(speed);
		mR.setSpeed(speed);
	}
	
	/**returns how far the wheel motors have turned
	 * @return the tachometer count
	 */
	public static int getTachoCount() {
		return mL.getTachoCount();
	}
}