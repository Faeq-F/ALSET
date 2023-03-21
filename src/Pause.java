import lejos.robotics.subsumption.Behavior;

public class Pause implements Behavior{
	
	//private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return Main.touch.pause;
	}

	@Override
	public void action() {
		movement.stop();
		System.out.println("Paused");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Could not sleep in paused behavior");
		}
	}

	@Override
	public void suppress() {
		//_suppressed = true;	
	}
	
}