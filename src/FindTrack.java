import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return (Main.connectedToPhone && Main.getMessageFromPhone() == "no_track_found");
	} 

	@Override
	public void action() {
		_suppressed = false;
		while (!_suppressed) {
			//go in a square
			//use methods that return immediately
			//so can ...
			if (_suppressed) break;
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