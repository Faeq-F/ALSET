import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

public class movement {
	
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
		mUltraSonic.rotate(90) ;
	}
	
	public static void SensRight() {
		mUltraSonic.rotate(-90) ;
	}
	
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	public static void turnLeft() {
		mL.startSynchronization();
		mL.forward();
		mR.backward();
		mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	public static void turnRight() {
		mL.startSynchronization();
		mL.backward();
		mR.forward();
		mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	public static void stop() {
		mL.startSynchronization();
		mL.stop();
		mR.stop();
		mL.endSynchronization();
	}
	
	public static void setSpeed(int speed) {
		mL.setSpeed(speed);
		mR.setSpeed(speed);
	}
}