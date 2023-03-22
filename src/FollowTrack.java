import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	//How long (ms) to go backward for before turning when there is no obstacle
	private final static long BackwardsTurnNoObstacle = 1240;
	//number of degrees to turn motors by, after turning when there is no obstacle
	private final static int forwardTurnNoObstacle = 500;
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {return !(Main.touch.pause);}

	@Override
	public void action() {
		_suppressed = false;	
		if (!_suppressed) {
			try {
				String m = Main.BTConnection.getMessageFromPhone();
				LCD.clear();
				LCD.drawString(m, 0, 3);
				if (m != null && !(m.contains("null"))){
					switch(m){
						case "forward":
							movement.forward();
							break;
						case "rotate_left":
							movement.turnLeft(BackwardsTurnNoObstacle, forwardTurnNoObstacle);
							break;
						case "rotate_right":
							movement.turnRight(BackwardsTurnNoObstacle, forwardTurnNoObstacle);
							break;
						default:
							Main.findTrack.action();
							break;
					}
				}
			} catch(Exception e) {
				//connection to phone is not established yet - let higher behavior deal with it
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
	
}