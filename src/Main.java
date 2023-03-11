import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	
	public static String PhoneIP = "10.0.1.2";
	public static int PhoneSocketPort = 1234;
	private static String messageFromPhone;
	
	public static Port TouchSensorPort = SensorPort.S4;
	public static float LOW_BATTERY = 0.005f;
	
	public static Port UltraSonicMotorPort = MotorPort.C;
	private static float DistanceFromObject;
	
	public static int speed = 200; //decided upon as an appropriate speed
	public static Port LeftMotorPort = MotorPort.A;
	public static Port RightMotorPort = MotorPort.D;

	public static void main(String[] args) {
		
		//Initializing behaviors
		
		
		//Initializing threads
		ExitThread CheckExit = new ExitThread();
		
		Arbitrator arbitrator = new Arbitrator(new Behavior[] {});
		
		//show main menu

		//start program
		Button.LEDPattern(1); //steady green light

		//starting threads
		CheckExit.start();

		//start movement
		arbitrator.go();

	}
	
	public static void setDistanceFromObject(float newDistanceFromObject) { DistanceFromObject = newDistanceFromObject; }
	
	public static float getDistanceFromObject() {return DistanceFromObject;}
	
	public static void setMessageFromPhone(String message) {messageFromPhone = message;}
	
	public static String getMessageFromPhone() {return messageFromPhone;}

}