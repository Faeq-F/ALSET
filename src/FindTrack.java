import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		try {
			return Main.connectedToPhone && Main.getMessageFromPhone().contains("no_track_found");
		} catch(NullPointerException e) {
			return false;
		}
	} 

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed) {
			movement.stop();
			System.out.println("running find track");
			//go in a square
			//use methods that return immediately
			//so can ...
			//after each step
			//after a square is done, go twice the length of a side of a square
			//(so that another square is done in front of this one in the next iteration)
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}