import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		try {
			return Main.connectedToPhone && Main.messageFromPhone.contains("no_track_found") && !(Main.touch.pause);
		} catch(NullPointerException e) {
			return false;
		}
	} 

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed) {
			movement.stop();
			System.out.println("cannot find track");
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}