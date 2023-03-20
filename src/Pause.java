import lejos.robotics.subsumption.Behavior;

public class Pause implements Behavior{
	
	private boolean _suppressed = false;
	private boolean pause = false;

	@Override
	public boolean takeControl() {
		return (Main.touched[0] == 1.0);
	}

	@Override
	public void action() {
		_suppressed = false;	
		while (!_suppressed) {
			Main.spTouch.fetchSample(Main.touched,0);
			if (pause && Main.touched[0] == 1.0) {
				pause = false;
				break;
			} else {
				movement.stop();
				pause = true;
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
	
}