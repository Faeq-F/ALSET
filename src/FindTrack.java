import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		try {
			return Main.getBTconnection().isConnectedToPhone()
				&& Main.getBTconnection().getMessageFromPhone().contains("no_track_found")
				&& !(Main.getTouch().isPaused());
		} catch(Exception e) {
			return false;
		}
	} 

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed) {
			movement.stop();
			LCD.clear();
			LCD.drawString("No Track Found", 0, 3);
			//go in pattern to find the track
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}