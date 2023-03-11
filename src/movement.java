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
	
	private static float DistanceFromObject;
	private static String messageFromPhone;
	
	public static void initializeAll() {
		mUltraSonic = new EV3LargeRegulatedMotor(UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		movement.setSpeed(speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
	}
	
	//This main method used for testing
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
	
	public static void setDistanceFromObject(float newDistanceFromObject) { 
		DistanceFromObject = newDistanceFromObject;
	}
	
	public static float getDistanceFromObject() {return DistanceFromObject;}
	
public static void setMessageFromPhone(String message) {messageFromPhone = message;}
	
	public static String getMessageFromPhone() {return messageFromPhone;}
	


}