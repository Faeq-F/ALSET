import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		try {
			return Main.BTConnection.getConnectedToPhone()
					&& Main.BTConnection.getMessageFromPhone().contains("no_track_found")
					&& !(Main.touch.pause);
		} catch(NullPointerException e) {
			return false;
		}
	} 

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed) {
			movement.stop();
			try {
				LCD.clear();
				LCD.drawString("No Track Found", 0, 3);
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