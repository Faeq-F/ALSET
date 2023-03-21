import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class FindTrack implements Behavior{
	
	private Boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		try {
			return Main.connectedToPhone && Main.messageFromPhone.contains("no_track_found") && !(Main.touch.pause);
		} catch(NullPointerException e) {
			return false;
		}
	} 

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed) {
			movement.stop();
			LCD.clear();
			LCD.drawString("No Track Found", 2, 3);
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}