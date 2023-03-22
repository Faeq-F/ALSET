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
				Main.ObjDet.closeSensor();
				Main.touch.closeSensor();
				try {
					Main.BTConnection.close();
				} catch (IOException e) {
					System.out.println("couldn't close socket");
					System.exit(0); //force close
				}
			}
		}
	}
}