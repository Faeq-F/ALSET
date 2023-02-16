import CarSelfDriver.Movement;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class movement {
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	//
	private static BaseRegulatedMotor sens;
	
	private static int speed = 200;
	
	//
	private static Port SensorMotor = MotorPort.C;
	
	private static Port LeftMotorPort = MotorPort.A;
	private static Port RightMotorPort = MotorPort.D;
	
	private static BaseRegulatedMotor sen;
	
	//This main method used for test 
	public static void main(String[] args) {
		
		//Initializing motors
		
		//
		sens = new EV3LargeRegulatedMotor(SensorMotor);
		
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		movement.setSpeed(speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
			
		//Testing code here:
		
	}
	
	public static void SensLeft() {
		movement.mL.startSynchronization();
		movement.sen.backward();
		movement.mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	public static void SensRight() {
		movement.mL.startSynchronization();
		movement.sen.forward();
		movement.mL.endSynchronization();
		Delay.msDelay(1000);
	
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	public static void turnLeft() {
		movement.mL.startSynchronization();
		movement.mL.backward();
		movement.mR.forward();
		movement.mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	public static void turnRight() {
		movement.mL.startSynchronization();
		movement.mL.forward();
		movement.mR.backward();
		movement.mL.endSynchronization();
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
