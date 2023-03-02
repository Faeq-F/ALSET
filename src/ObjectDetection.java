import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class ObjectDetection extends Thread{
	EV3UltrasonicSensor us_sensor = new EV3UltrasonicSensor(SensorPort.S2);
	SampleProvider sp = us_sensor.getDistanceMode();

	ObjectDetection(){}
	
	public void run(){
		float[] sample = new float[2] ;
		while (true){
			//read values from UltraSonic sensor and save to a field in movement
			sp.fetchSample(sample, 0) ;
			
			movement.setDistanceFromObject(sample[0]);
			
		}
	}
}