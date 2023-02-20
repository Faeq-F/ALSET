import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.Button;
import lejos.hardware.Battery;

public class ExitThread extends Thread{
	private static Port TouchSensorPort = SensorPort.S1;
	private static float LOW_BATTERY = 0.005f;
	ExitThread(){}
	
	public void run(){
		float[] arr = new float[1];
		EV3TouchSensor TouchSensor = new EV3TouchSensor(TouchSensorPort);
		while (true){
			//Exit on touch sensor
			TouchSensor.fetchSample(arr, MAX_PRIORITY);	
			float batteryVoltage = Battery.getVoltage();
			//Exit for program
			if(arr[0] == 1.0 || 
					Button.ESCAPE.isDown() ||
					(batteryVoltage < LOW_BATTERY)) {TouchSensor.close();System.exit(0);}
			
		}
	}
}


//check if the battery is low or the botton is pressed the system exit()