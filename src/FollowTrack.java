import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {return !(Main.touch.pause);} //Lowest behavior - should always return true

	@Override
	public void action() {
		_suppressed = false;	
		if (!_suppressed) {
			String m = Main.messageFromPhone;
			if (m != null && !(m.contains("null"))){
				switch(m){
					case "forward":
						movement.forward();
						break;
					case "rotate_left":
						movement.turnLeft(Main.BackwardsTurnNoObstacle, Main.forwardTurnNoObstacle);
						break;
					case "rotate_right":
						movement.turnRight(Main.BackwardsTurnNoObstacle, Main.forwardTurnNoObstacle);
						break;
					default:
						Main.findTrack.action();
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