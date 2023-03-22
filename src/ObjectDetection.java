import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class ObjectDetection extends Thread {

	private final static Port UltraSonicSensorPort = SensorPort.S1;
	private static EV3UltrasonicSensor UltraSonicSensor;
	private static float[] distance = new float[1];
	private static float DistanceFromObject;
	private static SampleProvider spUS;
	
	ObjectDetection(){
		UltraSonicSensor = new EV3UltrasonicSensor(UltraSonicSensorPort);
		spUS = UltraSonicSensor.getDistanceMode();
	}
	
	public void run() {
		while (true){
			try {
				spUS.fetchSample(distance, 0) ;
				DistanceFromObject = distance[0];
			} catch (Exception e) {
				break; //sensor was closed
			}
		}
	}
	
	public float getDistanceFromObject(){
		return DistanceFromObject;
	}
	
	public void closeSensor(){
		UltraSonicSensor.close();
	} 
}