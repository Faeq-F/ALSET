import lejos.robotics.subsumption.Behavior;

public class FollowTrack implements Behavior{
	
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
//		try {
//			return (Main.connectedToPhone) && !(Main.getMessageFromPhone().contains("no_track_found"));	
//		} catch(NullPointerException e) {
//			return false;
//		}
		return true;
	}

	@Override
	public void action() {
		_suppressed = false;	
		if (!_suppressed) {
			String m = Main.getMessageFromPhone();
			if (m != null && !(m.contains("null"))){
				System.out.println("a: "+m);
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