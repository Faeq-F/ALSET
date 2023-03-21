import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import lejos.robotics.subsumption.Behavior;

public class BluetoothConnection implements Behavior{
	
	private boolean _suppressed = false;

	@Override
	public boolean takeControl() {
		return !Main.connectedToPhone;
	}

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed){
			try {
				Main.clientSocket = new Socket(Main.PhoneIP, Main.PhoneSocketPort);
				Main.in = new BufferedReader(new InputStreamReader(Main.clientSocket.getInputStream()));
				Main.connectedToPhone = true;
			} catch(Exception e){
				System.out.println("failed to establish connection to phone - trying again");
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}
