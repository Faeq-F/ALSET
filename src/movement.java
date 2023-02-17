import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class movement {
	
	private static int speed = 200; //decided upon as an appropriate speed
	
	private static Port UltraSonicMotorPort = MotorPort.C;
	private static Port LeftMotorPort = MotorPort.A;
	private static Port RightMotorPort = MotorPort.D;
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	private static BaseRegulatedMotor mUltraSonic;
	
	//create getters and setters for this field
	private static float DistanceFromObject;
	
	//This main method used for test 
	public static void main(String[] args) {
		
		//Initializing motors
		mUltraSonic = new EV3LargeRegulatedMotor(UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		movement.setSpeed(speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
		
		//Testing code here:
		
		//Need to test UltraSonic sensor moving left 90 & right 90
		
		//Need to test robot moving 90 left & right
		
	}
	
	public static void SensLeft() {
		mUltraSonic.backward();
		Delay.msDelay(1000);
	}
	
	public static void SensRight() {
		mUltraSonic.forward();
		Delay.msDelay(1000);
	}
	
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	public static void turnLeft() {
		mL.startSynchronization();
		mL.backward();
		mR.forward();
		mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	public static void turnRight() {
		mL.startSynchronization();
		mL.forward();
		mR.backward();
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
