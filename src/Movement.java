

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
public class Movement {
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	
	
	//Car start moving forward
	public static void MoveForward() {
		Movement.mL.startSynchronization();
		Movement.mL.forward();
		Movement.mR.forward();
		Movement.mL.endSynchronization();
	}
	
	//Car turn left ninety degrees
	public static void TurnLeft() {
		Movement.mL.startSynchronization();
		Movement.mL.backward();
		Movement.mR.forward();
		Movement.mL.endSynchronization();
		Delay.msDelay(1000);
	}
	
	//Car turn right ninety degrees
	public static void TurnRight() {
		Movement.mL.startSynchronization();
		Movement.mL.forward();
		Movement.mR.backward();
		Movement.mL.endSynchronization();
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
	
	//This main method used for test 
	public static void main(String[] args) {
		
		//Initialising motors
		mL = new EV3LargeRegulatedMotor(MotorPort.A);
		mR = new EV3LargeRegulatedMotor(MotorPort.D);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
		Movement.setSpeed(200); //Good initial speed
		
		//Initialising behaviours
	
		
		//Initialising threads
	
		
		//Creating Arbitrator

		
		//starting threads
		
		
		//starting arbitrator	
		
	}
	

}
