import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{

	@Override
	public boolean takeControl() {return true;} //Lowest behavior - should always return true

	@Override
	public void action() {
		
	}

	@Override
	public void suppress() {
		
	}

}