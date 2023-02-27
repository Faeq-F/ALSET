import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class FollowTrack implements Behavior{
	
	//using these vars as the fields to be checked until bluetooth code has been written
	Boolean BTconnection = true;
	Boolean TrackFound = true;
	Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		//check if there is a bluetooth connection and if a track is found (just use the fields)
		//not used for testing
		if(BTconnection == true && TrackFound == true)
			return true;
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
		//check if suppressed
		
		if(messageC == "stay_in_the_center") {
			movement.forward();
			Delay.msDelay(1000);
		}
		else if(messageL == "turn_left") {
			movement.turnLeft();
			Delay.msDelay(1000);
		}
		else if(messageR == "turn_right") {
			movement.turnRight();
			Delay.msDelay(1000);
		}
		//if not suppressed, follow message instructions
		else
			suppress();
		
	}

	@Override
	public void suppress() {
		//change suppress field
		//not used for testing
		_suppressed = true;
	}
	
	public static void main(String[] args) {
		//run test:
		FollowTrack FT = new FollowTrack();
		FT.action();
	}

}
