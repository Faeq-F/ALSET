import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		//check if track is found & BT connected
		return true;
	} 

	@Override
	public void action() {
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