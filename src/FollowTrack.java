import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	//using these vars as the fields to be checked until bluetooth code has been written
	Boolean connectionT = true;
	Boolean connectionF = false;
	Boolean TrackFoundT = true;
	Boolean TrackFoundF = false;

	@Override
	public boolean takeControl() {
		//check if there is a bluetooth connection and if a track is found (just use the fields)
		return false;
		
	}

	@Override
	public void action() {
		//move based on message from phone
		//String message = movement.getMessageFromPhone();
		//Use the following vars instead for testing:
		String messageC = "stay_in_the_center";
		String messageL = "turn_left";
		String messageR = "turn_right";
		
	}

	@Override
	public void suppress() {
		
	}
	
	public static void main(String[] args) {
		//run test:
		FollowTrack FT = new FollowTrack();
		FT.action();
	}

}
