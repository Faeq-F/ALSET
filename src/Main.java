import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.lcd.LCD;

public class Main {
	
	public static String PhoneIP = "10.0.1.2";
	public static int PhoneSocketPort = 1234;
	private static String messageFromPhone;
	public static BluetoothConnection BTConnection;
	public static boolean connectedToPhone = false; 
	public static boolean TrackFound = true;
	
	public static Port TouchSensorPort = SensorPort.S4;
	public static float LOW_BATTERY = 0.005f;
	
	public static Port UltraSonicMotorPort = MotorPort.C;
	private static float DistanceFromObject;
	
	public static int speed = 200; //decided upon as an appropriate speed
	public static Port LeftMotorPort = MotorPort.A;
	public static Port RightMotorPort = MotorPort.D;

	public static void main(String[] args) {
		
		//Initializing behaviors
		FindTrack findTrack = new FindTrack();
		ObjectTraversal objectTraversal = new ObjectTraversal();
		FollowTrack followTrack = new FollowTrack();
		BTConnection = new BluetoothConnection();
		
		//Initializing threads
		ExitThread CheckExit = new ExitThread(); 
		BluetoothInfo BTInfo = new BluetoothInfo();
		ObjectDetection ObjDet = new ObjectDetection();
		
		//initialize arbitrator with behaviors
		Arbitrator arbitrator = new Arbitrator(
				new Behavior[] {BTConnection, findTrack, followTrack, objectTraversal}
		);
		
		//show welcome screen with author names and version info
		LCD.clear();
		LCD.drawString("Welcome", 0 , 0 );
		LCD.drawString("Verson 17.1", 0 , 1);
		
		Button.LEDPattern(5); //flashing orange light
		
		//wait for key press
		while(true){
			if(Button.DOWN != null) break;
		}
		
		//starting threads
		BTInfo.start();
		ObjDet.start();
		CheckExit.start();
		
		Button.LEDPattern(1); //steady green light
		
		//start movement
		arbitrator.go();

	}
	
	public static void setDistanceFromObject(float newDistanceFromObject) { DistanceFromObject = newDistanceFromObject; }
	
	public static float getDistanceFromObject() {return DistanceFromObject;}
	
	public static void setMessageFromPhone(String message) {messageFromPhone = message;}
	
	public static String getMessageFromPhone() {return messageFromPhone;}

}