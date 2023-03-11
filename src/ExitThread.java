import lejos.hardware.sensor.NXTTouchSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.hardware.Battery;

public class ExitThread extends Thread{
	
	ExitThread(){}
	
	@Override
	public void run(){
		
		NXTTouchSensor TouchSensor = new NXTTouchSensor (Main.TouchSensorPort);
		SampleProvider spTouch = TouchSensor.getTouchMode();
		float[] touched = new float[1];
		
		while (true){
			spTouch.fetchSample(touched,0);
			//Exit for program
			if(touched[0] ==1.0 || Button.ESCAPE.isDown() || (Battery.getVoltage() < Main.LOW_BATTERY)) {
				TouchSensor.close();
				System.exit(0);
			}
		}
	}
}