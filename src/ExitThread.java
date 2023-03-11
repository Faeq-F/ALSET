import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
//import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.hardware.Battery;
import lejos.hardware.sensor.NXTTouchSensor;

public class ExitThread extends Thread{
	private static Port TouchSensorPort = SensorPort.S4;
	private static float LOW_BATTERY = 0.005f;
	ExitThread(){}
	
	public void run(){
		//float[] touched = new float[1];
		NXTTouchSensor TouchSensor = new NXTTouchSensor (TouchSensorPort);
		SampleProvider sp = TouchSensor.getTouchMode();
		float[] touched = new float[1];
		while (true){
			float batteryVoltage = Battery.getVoltage();
			sp.fetchSample(touched,0);
			//Exit for program
			
			if(touched[0] ==1.0 || Button.ESCAPE.isDown() || (batteryVoltage < LOW_BATTERY)) {
				TouchSensor.close();
				System.exit(0);
			}

		}
	}
}

			
			
