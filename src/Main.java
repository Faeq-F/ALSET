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
	
	public final static String PhoneIP = "10.0.1.2";
	public final static int PhoneSocketPort = 1234;
	public static Socket clientSocket;
	public static BufferedReader in;
	public static String messageFromPhone;
	public static BluetoothConnection BTConnection;
	public static boolean connectedToPhone = false;
	
	private final static Port TouchSensorPort = SensorPort.S4;
	public static NXTTouchSensor TouchSensor;
	public static SampleProvider spTouch;
	public static float[] touched = new float[1];
	public final static float LOW_BATTERY = 0.005f;
	
	public final static Port UltraSonicMotorPort = MotorPort.C;
	private final static Port UltraSonicSensorPort = SensorPort.S1;
	public static EV3UltrasonicSensor UltraSonicSensor;
	public static SampleProvider spUS;
	public static float[] distance = new float[1];
	private static float DistanceFromObject;
	/**
	 * How close an obstruction should be from the robot before this behavior is executed.
	 */
	public final static float distanceFromObject = 0.126f;
	
	/**
	 * delay to prevent further code from running too early in ObjectTraversal
	 */
	public final static int delayOT = 1100;
	
	//storing motor revolutions
	public static int tach_start, tach_end;
	
	/**
	 * How long (ms) to go backward for before turning when there is no obstacle
	 */
	public final static long BackwardsTurnNoObstacle = 1250;//1085
	public final static int forwardTurnNoObstacle = 500;
	public final static int speed = 200; //decided upon as an appropriate speed
	public final static Port LeftMotorPort = MotorPort.A;
	public final static Port RightMotorPort = MotorPort.D;
	/**
	 * how much the motor needs to rotate by (degrees) to make the robot turn ~90 degrees
	 */
	public final static int ROT90DEGREES = 227;//235 original
	/**
	 * The motor connected to the left wheel of the robot
	 */
	public static BaseRegulatedMotor mL;
	public static BaseRegulatedMotor mR;
	public static BaseRegulatedMotor mUltraSonic;
	
	public static FindTrack findTrack;

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
		BTConnection = new BluetoothConnection();
		Pause pause = new Pause();
		
		//Initializing threads
		ExitThread CheckExit = new ExitThread(); 
		BluetoothInfo BTInfo = new BluetoothInfo();
		ObjectDetection ObjDet = new ObjectDetection();
		TouchThread touch = new TouchThread();
		
		//initialize arbitrator with behaviors
//		Arbitrator arbitrator = new Arbitrator(
//				new Behavior[] {BTConnection, findTrack, followTrack, objectTraversal}
//		);
		

		Button.LEDPattern(4); //flashing green light
		//show welcome screen with author names and version info
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		LCD.clear();
		g.clear();
		g.drawString("Welcome to ALSET", 5, 0, 0);
		g.setFont(Font.getSmallFont());
		g.drawString("A path following robot", 2, 20, 0);
		g.drawString("", 2, 30, 0);
		g.drawString("Version 17.1", 2, 40, 0);
		g.drawString("", 2, 50, 0);
		g.drawString("Authors:", 2, 60, 0);
		g.drawString("Faeq Faisal", 2, 70, 0); 
		g.drawString("Leonardo Loureiro", 2, 80, 0);
		g.drawString("Morris Sardo", 2, 90, 0);
		
		// Enter GUI button:
		g.fillRect(55, 100, 23, 23);
		g.drawString("Calibration Instructions", 60, 107, 0,true);
		//LCD.drawString("Welcome", 0 , 0 );
		//LCD.drawString("Version 17.1", 0 , 1);
		
		//wait for key press
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("Calibration", 5, 0, 0);
		g.setFont(Font.getSmallFont());
		g.drawString("If you have not already", 2, 20, 0);
		g.drawString("connect your android phone", 2, 30, 0);
		g.drawString("with the robot and enable", 2, 40, 0);
		g.drawString("reverse tethering. Then open", 2, 50, 0);
		g.drawString("the ALSET app and place the ", 2, 60, 0);
		g.drawString("robot on the track it needs", 2, 70, 0); 
		g.drawString("to follow. Tap the track on", 2, 80, 0);
		g.drawString("the app to calibrate.", 2, 90, 0);
		
		// Enter GUI button:
		g.fillRect(55, 100, 23, 23);
		g.drawString("App is calibrated", 60, 107, 0,true);
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		g.clear();
		Arbitrator arbitrator = new Arbitrator(
				new Behavior[] {followTrack, findTrack, objectTraversal, pause, BTConnection}
		);
		
		//starting threads
		BTInfo.start();
		ObjDet.start();
		CheckExit.start();
		touch.start();
		
		Button.LEDPattern(1); //steady green light
		
		//start movement
		arbitrator.go();

	}
	
	public static void setDistanceFromObject(float newDistanceFromObject) { DistanceFromObject = newDistanceFromObject; }
	
	public static float getDistanceFromObject() {return DistanceFromObject;}
	
	public static void setMessageFromPhone(String message) {
		System.out.println("setting message: "+message);
		messageFromPhone = message;}
	
	public static String getMessageFromPhone() {return messageFromPhone;}

}