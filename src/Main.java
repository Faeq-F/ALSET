import java.io.BufferedReader;
import java.net.Socket;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class Main {
	
	public final static float LOW_BATTERY = 0.005f;
	
	private static GraphicsLCD gLCD = LocalEV3.get().getGraphicsLCD();
	private static int textX = 2; //give text a little bit of padding on-screen
	
	public final static String PhoneIP = "10.0.1.2";
	public final static int PhoneSocketPort = 1234;
	public static boolean connectedToPhone = false;
	public static String messageFromPhone;
	public static Socket clientSocket;
	public static BufferedReader in;
	
	private final static Port TouchSensorPort = SensorPort.S4;
	public static float[] touched = new float[1];
	public static NXTTouchSensor TouchSensor;
	public static SampleProvider spTouch;
	public static TouchThread touch;
	
	private final static Port UltraSonicSensorPort = SensorPort.S1;
	private final static Port UltraSonicMotorPort = MotorPort.C;
	public static EV3UltrasonicSensor UltraSonicSensor;
	public static float[] distance = new float[1];
	private static float DistanceFromObject;
	public static SampleProvider spUS;
	//How close an obstruction should be from the robot before the object traversal behavior is executed.
	public final static float distanceFromObject = 0.126f;
	//delay to prevent further code from running too early in ObjectTraversal
	public final static int delayOT = 1700;
	
	public final static Port LeftMotorPort = MotorPort.A;
	public final static Port RightMotorPort = MotorPort.D;
	public final static int speed = 200; //decided upon as an appropriate speed
	//How long (ms) to go backward for before turning when there is no obstacle
	public final static long BackwardsTurnNoObstacle = 1240;
	//number of degrees to turn motors by, after turning when there is no obstacle
	public final static int forwardTurnNoObstacle = 500;
	//how much the motor needs to rotate by (degrees) to make the robot turn ~90 degrees
	public final static int ROT90DEGREES = 227;
	// motors for the robot
	public static BaseRegulatedMotor mL;
	public static BaseRegulatedMotor mR;
	public static BaseRegulatedMotor mUltraSonic;
	//find track behavior for when followTrack can't run
	public static FindTrack findTrack;
	//Pause behavior for when user touches the touchSensor
	public static Pause pause;

	public static void main(String[] args) {
		
		//Initializing motors
		mUltraSonic = new EV3MediumRegulatedMotor(Main.UltraSonicMotorPort);
		mL = new EV3LargeRegulatedMotor(Main.LeftMotorPort);
		mR = new EV3LargeRegulatedMotor(Main.RightMotorPort);
		movement.setSpeed(Main.speed);
		mL.synchronizeWith(new BaseRegulatedMotor[] {mR});
		
		//Initializing sensors
		TouchSensor = new NXTTouchSensor(TouchSensorPort);
		UltraSonicSensor = new EV3UltrasonicSensor(UltraSonicSensorPort);
		//Initializing sample providers
		spTouch = TouchSensor.getTouchMode();
		spUS = UltraSonicSensor.getDistanceMode();
		
		//Initializing behaviors
		findTrack = new FindTrack();
		ObjectTraversal objectTraversal = new ObjectTraversal();
		FollowTrack followTrack = new FollowTrack();
		BluetoothConnection BTConnection = new BluetoothConnection();
		pause = new Pause();
		
		//Initializing threads
		ExitThread CheckExit = new ExitThread(); 
		BluetoothInfo BTInfo = new BluetoothInfo();
		ObjectDetection ObjDet = new ObjectDetection();
		touch = new TouchThread();
		
		//flashing green light
		Button.LEDPattern(4);
		LCD.clear();
		
		//show welcome screen
		showWelcome();
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		
		//show calibration instructions
		showInstructions();
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		
		gLCD.clear();
		//start program
		Arbitrator arbitrator = new Arbitrator(
				new Behavior[] {pause, followTrack, findTrack, objectTraversal, BTConnection}
		);
		
		//starting threads
		BTInfo.start();
		ObjDet.start();
		CheckExit.start();
		touch.start();
		
		//steady green light
		Button.LEDPattern(1);
		
		//start movement
		arbitrator.go();
	}
	
	public static void showWelcome() {
		gLCD.clear();
		//title of screen
		gLCD.setFont(Font.getDefaultFont());
		gLCD.drawString("Welcome to ALSET", 5, 0, 0);
		//text for screen
		gLCD.setFont(Font.getSmallFont());//y coords increase by 10
		gLCD.drawString("A path following robot", textX, 20, 0);
		gLCD.drawString("", textX, 30, 0);
		gLCD.drawString("Version 23.7", textX, 40, 0);
		gLCD.drawString("", textX, 50, 0);
		gLCD.drawString("Authors:", textX, 60, 0);
		gLCD.drawString("Faeq Faisal", textX, 70, 0); 
		gLCD.drawString("Leonardo Loureiro", textX, 80, 0);
		gLCD.drawString("Morris Sardo", textX, 90, 0);
		// Continue button
		gLCD.fillRect(55, 100, 23, 23);
		gLCD.drawString("Calibration", 60, 107, 0, true);
	}
	
	public static void showInstructions(){
		gLCD.clear();
		//title of screen
		gLCD.setFont(Font.getDefaultFont());
		gLCD.drawString("Calibration", 5, 0, 0);
		//text for screen
		gLCD.setFont(Font.getSmallFont());//y coords increase by 10 
		gLCD.drawString("If you have not already done", textX, 20, 0);
		gLCD.drawString("so, open the ALSET app and", textX, 30, 0);
		gLCD.drawString("place the robot on the track", textX, 40, 0);
		gLCD.drawString("Use the middle rectangle as a", textX, 50, 0);
		gLCD.drawString("guide. Tap the track, on", textX, 60, 0);
		gLCD.drawString("screen, to calibrate the app;", textX, 70, 0); 
		gLCD.drawString("you may use the flashlight", textX, 80, 0);
		gLCD.drawString("for better recognition.", textX, 90, 0);
		// Continue button
		gLCD.fillRect(55, 100, 23, 23);
		gLCD.drawString("Execute", 60, 107, 0, true);
	}
	
	public static void setDistanceFromObject(float newDistanceFromObject){
		DistanceFromObject = newDistanceFromObject;
	}
	
	public static float getDistanceFromObject(){
		return DistanceFromObject;
	}

}