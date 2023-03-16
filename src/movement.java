import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

public class movement {
	
	/**
	 * Value which makes robots turn a ~90 degree turn
	 */
	final static int ROT90DEGREES = 235 ;
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	private static BaseRegulatedMotor mUltraSonic;
	
	public static void initializeAll() {
		mUltraSonic = new EV3LargeRegulatedMotor(Main.UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(Main.LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(Main.RightMotorPort);
		movement.setSpeed(Main.speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
	}
	
	//testing method
	public static void main(String[] args) {
		forward();
		stop();
		forward();
		turnLeft();
		stop();
		forward();
		turnRight();
		stop();	
	}
	
	public static void SensLeft() {
		mUltraSonic.rotate(-100) ;
	}
	
	public static void SensRight() {
		mUltraSonic.rotate(100) ;
	}
	
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	public static void forward(int distance_to_travel) {
		mL.startSynchronization() ;
		
		mL.rotate(distance_to_travel, true) ;
		mR.rotate(distance_to_travel, true) ;
		
		mL.endSynchronization() ;
		
		mL.waitComplete() ;
		mR.waitComplete() ;
	}
	
	public static void backward() {
		mL.startSynchronization() ;
		
		mL.stop() ;
		mR.stop() ;
		
		mL.backward() ;
		mR.backward() ;
		
		Delay.msDelay(500) ;
		
		mL.endSynchronization() ;
		
		mL.waitComplete() ;
		mR.waitComplete() ;
	}
	
	public static void turnLeft() {
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR}) ;
		
		mL.rotate(ROT90DEGREES, true);
		mR.rotate(-ROT90DEGREES, true);
		
		mL.endSynchronization() ;	
		
		mL.waitComplete() ;
		mR.waitComplete() ;
	}
	
	public static void turnRight() {
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR}) ;
		
		mL.rotate(-ROT90DEGREES, true) ;
		mR.rotate(ROT90DEGREES, true) ;
		
		mL.endSynchronization() ;	
		
		mL.waitComplete() ;
		mR.waitComplete() ;
	}
	
	public static void stop() {
		mL.startSynchronization();
		mL.stop(true) ;
		mR.stop(true) ;
		mL.endSynchronization();
	}
	
	public static void setSpeed(int speed) {
		mL.setSpeed(speed);
		mR.setSpeed(speed);
	}
	
	public static int getTachoCount() {
		return mL.getTachoCount() ;
	}
}