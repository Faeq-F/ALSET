import lejos.robotics.subsumption.Behavior;

public class Pause implements Behavior{

	@Override
	public boolean takeControl() {return true;} //Lowest behavior - should always return true

	@Override
	public void action() {
		movement.stop();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Could not sleep in paused behavior");
		}
	}

	@Override
	public void suppress() {} // no use as pause is handled by the touch sensor's thread
	
}