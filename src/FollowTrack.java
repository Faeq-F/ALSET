import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return (Main.connectedToPhone && !(Main.getMessageFromPhone() == "no_track_found"));
	}

	@Override
	public void action() {
		//check if suppressed
		while (!_suppressed) {
			//if not suppressed, follow message instructions
			switch(Main.getMessageFromPhone()){
				case "forward":
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