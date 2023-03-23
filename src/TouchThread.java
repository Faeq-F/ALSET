import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.robotics.SampleProvider;

public class TouchThread extends Thread{
	
	private final static Port touchSensorPort = SensorPort.S4;
	private static float[] touched = new float[1];
	private static NXTTouchSensor touchSensor;
	private static SampleProvider spTouch;
	private boolean pause = false;
	
	TouchThread(){
		touchSensor = new NXTTouchSensor(touchSensorPort);
		spTouch = touchSensor.getTouchMode();
	}
	
	public void run(){
		while (true) {
			try {
				spTouch.fetchSample(touched, 0);
				boolean pressed = touched[0] == 1.0;
				if (pressed && (!pause)) {
					movement.stop();
					pause = true;
				} else if (pressed && pause)
					pause = false;
				else if (pause)
					movement.stop();
			} catch (Exception e) {
				break; //sensor was closed
			}
		}
	}
	
	public boolean isPaused(){
		return pause;
	}
	
	public void setPause(boolean b){
		pause = b;
	}
	
	public void closeSensor(){
		touchSensor.close();
	}
}
