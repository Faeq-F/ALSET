import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class ObjectTraversal implements Behavior{

	@Override
	public boolean takeControl() {
		return false;
		
	}

	@Override
	public void action() {
		
	}

	@Override
	public void suppress() {
		
	}
	
	// NOTE: 
	//	- To make robot rotate ~90 degrees on the spot, make it rotate by 200 amounts.
	//	- The ultrasonic sensor motor's turn is clockwise relative to the top of the robot,
	//	  meaning that 90 makes it turn to its right, 0 to the front and so on. 
	//	- REMEMBER, ultrasonic's motor is set 0 as when PROGRAM STARTS, there is no absolute.	
	final static int SENSORMOTOR90TURN = 100 ;
	
	final static float WALLLENFROMROBOT = 0.126f ;
	
	// about 235...
	final static int ROT90DEGREES = 235 ;
	
	final static int DELAYBY = 1000 ;
	
	public static void main(String[] args) {
		ObjectDetection OD = new ObjectDetection() ;
		
		OD.start();
		
		// ULTRASONIC SENSOR PARTS
		// assign tacho to vars
		int tach_start, tach_end ;
				
		// ULTRASONIC SENSOR MOTOR
		BaseRegulatedMotor distanceSensorMotor = new EV3MediumRegulatedMotor(MotorPort.C) ;
		// /ULTRASONIT SENSOR MOTOR
		
		// WHEEL PARTS
		BaseRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A) ;
		BaseRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.D) ;
		
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		// make a ~90 degree turn on the stop		
		mLeft.rotate(ROT90DEGREES, true) ;
		mRight.rotate(-ROT90DEGREES, true) ;
		mLeft.endSynchronization() ;
		
		mLeft.waitComplete() ;
		mRight.waitComplete() ;		
		
		// Also turn distance detection so that it points
		// towards wall.
		distanceSensorMotor.rotateTo(SENSORMOTOR90TURN) ;		
		
		// 		KEEP GOING UNTIL WALL IS NO LONGER DETECTED
		// Get starting tacho count, this will be used later
		tach_start = mLeft.getTachoCount() ;
		
		// keep going until no more obstruction detected
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.forward() ;
		mRight.forward() ;
		mLeft.endSynchronization() ;
		
		
		float current_distance ;
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLLENFROMROBOT) {
				break;
			}
		}
		
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
		
		// assign tacho count to a var
		tach_end = mLeft.getTachoCount() ;		
		
		// stop robot
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.stop(true) ;
		mRight.stop(true) ;
		mLeft.endSynchronization() ;
		
		
		// turn -90, do NOT change distance sensor position
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.rotate(-ROT90DEGREES, true) ;
		mRight.rotate(ROT90DEGREES, true) ;
		
		mLeft.waitComplete() ;
		mRight.waitComplete() ;
		
		mLeft.forward() ;
		mRight.forward() ;
		
		mLeft.endSynchronization() ;
		
				
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
		
		// keep going until wall is not detected on the side
		// as obstruction may be a box.
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLLENFROMROBOT) {
				break;
			}	
		}
		
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
				
		// stop robot
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.stop(true) ;
		mRight.stop(true) ;
		// mLeft.waitComplete() ;
		// mRight.waitComplete() ;
		mLeft.endSynchronization() ;
		
		
		
		// turn -90 again
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.rotate(-ROT90DEGREES, true) ;
		mRight.rotate(ROT90DEGREES, true) ;
		
		mLeft.waitComplete() ;
		mRight.waitComplete() ;
		
		mLeft.endSynchronization() ;
		
				
		// go forward the amount of tacho count we assigned before
		float distance_travelled = tach_end - tach_start ;
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.rotate((int) distance_travelled, true) ;
		mRight.rotate((int) distance_travelled, true) ;
		
		mLeft.waitComplete() ;
		mRight.waitComplete() ;
		
		mLeft.endSynchronization() ;
		
		// then rotate 90. In theory, we should be back where robot 
		// was when it was behind the box/obstruction.
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight}) ;
		mLeft.rotate(ROT90DEGREES, true) ;
		mRight.rotate(-ROT90DEGREES, true) ;
		
		mLeft.waitComplete() ;
		mRight.waitComplete() ;
		
		mLeft.endSynchronization() ;
		
		distanceSensorMotor.rotateTo(SENSORMOTOR90TURN*0) ;	
		
		mLeft.close() ;
		mRight.close() ;
		
		distanceSensorMotor.close() ;		
		
		OD.stop_thread = true ; 

	}

}
