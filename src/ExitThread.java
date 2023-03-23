import lejos.hardware.Button;
import java.io.IOException;
import lejos.hardware.Battery;

public class ExitThread extends Thread{
	
	private final static float LOW_BATTERY = 0.005f;
	
	ExitThread(){}
	
	@Override
	public void run(){
		while (true){
			if(Button.ESCAPE.isDown() || (Battery.getVoltage() < LOW_BATTERY)) {
				System.exit(0);
				//close all sensors & connections
				Main.getUSval().closeSensor();
				Main.getTouch().closeSensor();
				try {
					Main.getBTconnection().close();
				} catch (IOException e) {
					System.out.println("socket already closed");
				}
				//exit program
				System.exit(0);
			}
		}
	}
}