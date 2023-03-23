import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class Pause implements Behavior{

	@Override
	public boolean takeControl() {
		return true; //Lowest behavior - should always return true
	}

	@Override
	public void action() {
		movement.stop();
		LCD.drawString("Paused", 0, 2);
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			System.out.println("Could not sleep in paused behavior");
		}
	}

	@Override
	public void suppress() {} // no use as pause is handled by the touch sensor's thread
	
}