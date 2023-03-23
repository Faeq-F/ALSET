import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;

public class Main {
	
	private static GraphicsLCD gLCD = LocalEV3.get().getGraphicsLCD();
	private static int textX = 2; //give text a bit of padding on-screen

	public static void main(String[] args) {
		
		//initializing motors (run independent)
		movement.initialiseAll();
		
		//Initializing threads (run independent)
		ExitThread CheckExit = new ExitThread(); 
		BluetoothInfo BTInfo = new BluetoothInfo();
		ObjDet = new ObjectDetection();
		touch = new TouchThread();
		
		//Initializing behaviors (run independent)
		findTrack = new FindTrack();
		ObjectTraversal objectTraversal = new ObjectTraversal();
		FollowTrack followTrack = new FollowTrack();
		BTConnection = new BluetoothConnection();
		pause = new Pause();
		
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
		LCD.clear();
		gLCD.clear();
		
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
	
	/**
	 * Shows the welcome screen on the EV3 LCD display
	 */
	public static void showWelcome() {
		gLCD.clear();
		//title of screen
		gLCD.setFont(Font.getDefaultFont());
		gLCD.drawString("Welcome to ALSET", 5, 0, 0);
		//text for screen (line height is 10 with small font)
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
		gLCD.fillRect(55, 100, 23, 23);//x, y, w, h randomly chosen - doesn't matter, just visual
		gLCD.drawString("Calibration", 60, 107, 0, true);
	}
	
	/**
	 * Shows the calibration instructions screen on the EV3 LCD display.
	 * (To help initial, run dependent calibration)
	 */
	public static void showInstructions(){
		gLCD.clear();
		//title of screen
		gLCD.setFont(Font.getDefaultFont());
		gLCD.drawString("Calibration", 5, 0, 0);
		//text for screen (line height is 10 with small font)
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
		gLCD.fillRect(55, 100, 23, 23);//x, y, w, h randomly chosen - doesn't matter, just visual
		gLCD.drawString("Execute", 60, 107, 0, true);
	}
	
	private static ObjectDetection ObjDet;
	public static ObjectDetection getUSval(){
		return ObjDet;
	}
	
	private static TouchThread touch;
	public static TouchThread getTouch(){
		return touch;
	}
	
	private static Pause pause;
	public static Pause getPause(){
		return pause;
	}
	
	private static FindTrack findTrack;
	public static FindTrack getFTbehavior(){
		return findTrack;
	}
	
	private static BluetoothConnection BTConnection;
	public static BluetoothConnection getBTconnection(){
		return BTConnection;
	}

}