import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	//using these vars as the fields to be checked until BT code has been written
	Boolean BTconnection = true;
	Boolean TrackFound = true;
	Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		//check if there is a BT connection and if a track is found (just use the fields)
		//not used for testing
		if(BTconnection == true && TrackFound == true)
			return true;
		return false;
	}

	@Override
	public void action() {
		//move based on message from phone
		String message = Main.getMessageFromPhone();
		//check if suppressed
		while (!_suppressed) {
			//if not suppressed, follow message instructions
			switch(message){
				case "stay_in_the_center":
					movement.forward();
					break;
				case "turn_left":
					movement.turnLeft();
					break;
				case "turn_right":
					movement.turnRight();
					break;
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
	
	//testing method
	public static void main(String[] args) {
		movement.initializeAll();
		ExitThread checkExit = new ExitThread();
		//run action() for testing:
		FollowTrack FT = new FollowTrack();
		checkExit.start();
		FT.action();
	}
}