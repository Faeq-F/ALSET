import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	//How long (ms) to go backward for before turning when there is no obstacle
	private final static long backward = 1240;
	//number of degrees to turn motors by, after turning when there is no obstacle
	private final static int forward = 500;
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return !(Main.getTouch().isPaused()) && Main.getBTconnection().isConnectedToPhone();
	}

	@Override
	public void action() {
		_suppressed = false;	
		if (!_suppressed) {
			String m = Main.getBTconnection().getMessageFromPhone();
			if (m != null && !(m.contains("null"))){
				LCD.clear();
				LCD.drawString(m, 0, 3);
				switch(m){
				case "forward":
					movement.forward();
					break;
				case "rotate_left":
					movement.turnLeft(backward, forward);
					break;
				case "rotate_right":
					movement.turnRight(backward, forward);
					break;
				default://increased reliability
					Main.getFTbehavior().action();
					break;
				}
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
	
}