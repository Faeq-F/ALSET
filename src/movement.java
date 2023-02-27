import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class movement {
	
	final static float WHEEL_DIAMETER = 51; // The diameter (mm) of the wheels
	final static float AXLE_LENGTH = 44; // The distance (mm) your two driven wheels
	final static float ANGULAR_SPEED = 100; // How fast around corners (degrees/sec)
	final static float LINEAR_SPEED = 70; // How fast in a straight line (mm/sec)
	
	private static Port UltraSonicMotorPort = MotorPort.C;
	private static Port LeftMotorPort = MotorPort.D;
	private static Port RightMotorPort = MotorPort.A;
	
	private static BaseRegulatedMotor mL;
	private static BaseRegulatedMotor mR;
	private static BaseRegulatedMotor mUltraSonic;
	
	private static MovePilot pilot;
	
	private static float DistanceFromObject;
	private static String messageFromPhone;
	
	//This main method used for test 
	public static void main(String[] args) {
		
		//Initializing motors
		mUltraSonic = new EV3LargeRegulatedMotor(UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(RightMotorPort);
		
		// Create a ”Wheel” with Diameter 51mm and offset 22mm left of center.
		Wheel wLeft = WheeledChassis.modelWheel(mL, WHEEL_DIAMETER).offset(-AXLE_LENGTH / 2);
				
		// Create a ”Wheel” with Diameter 51mm and offset 22mm right of center.
		Wheel wRight = WheeledChassis.modelWheel(mR, WHEEL_DIAMETER).offset(AXLE_LENGTH / 2);
		
		// Create a ”Chassis” with two wheels on it.
		Chassis chassis = new WheeledChassis((new Wheel[] {wRight, wLeft}), WheeledChassis.TYPE_DIFFERENTIAL);
		
		// Finally create a pilot which can drive using this chassis.
		pilot = new MovePilot(chassis);
		
		//setting speeds
		setSpeed(LINEAR_SPEED, ANGULAR_SPEED);
		
		//Testing code here:
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
		pilot.forward();
	}
	
	public static void turnLeft() {
		pilot.rotate(190);
	}
	
	public static void turnRight() {
		pilot.rotate(-190);
	}
	
	public static void stop() {
		pilot.stop();
	}
	
	public static void setSpeed(float linearSpeed, float angularSpeed) {
		pilot.setAngularSpeed(linearSpeed);
		pilot.setLinearSpeed(angularSpeed) ;
	}
	
	public static void setMessageFromPhone(String message) {messageFromPhone = message;}
	
	public static String getMessageFromPhone() {return messageFromPhone;}
	
	public static void setDistanceFromObject(float newDistanceFromObject) { 
		DistanceFromObject = newDistanceFromObject;
	}
	
	public static float getDistanceFromObject() {return DistanceFromObject;}

}
