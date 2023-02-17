import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class ExitThread extends Thread{
	private static Port TouchSensorPort = SensorPort.S1;
	//Initialize touch sensor
	
	ExitThread(){}
	
	public void run(){
		while (true){
			//Exit for program
			
		}
	}
}