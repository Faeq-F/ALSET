import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class movement {
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	private static int speed = 200;
	
	private static Port LeftMotorPort = MotorPort.A;
	private static Port RightMotorPort = MotorPort.D;
	
	//This main method used for test 
	public static void main(String[] args) {
		
		//Initializing motors
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		movement.setSpeed(speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
			
		//Testing code here:
		
	}
	
	public static void forward() {
		mL.startSynchronization();
		mL.forward();
		mR.forward();
		mL.endSynchronization();
	}
	
	//Car turn left ninety degrees
	public static void turnLeft() {
		movement.mL.startSynchronization();
		movement.mL.backward();
		movement.mR.forward();
		movement.mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	//Car turn right ninety degrees
	public static void turnRight() {
		movement.mL.startSynchronization();
		movement.mL.forward();
		movement.mR.backward();
		movement.mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	//Car stop
	public static void stop() {
		mL.startSynchronization();
		mL.stop();
		mR.stop();
		mL.endSynchronization();
	}
	
	//Set the speed of the car
	public static void setSpeed(int speed) {
		mL.setSpeed(speed);
		mR.setSpeed(speed);
	}

}
