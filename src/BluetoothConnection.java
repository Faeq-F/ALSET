import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import lejos.robotics.subsumption.Behavior;

public class BluetoothConnection implements Behavior{
	
	private Boolean _suppressed = false;
	private Socket clientSocket;
	public BufferedReader in;

	@Override
	public boolean takeControl() {return true;}//Lowest behavior - should always return true

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed){
			try {
				clientSocket = new Socket(Main.PhoneIP, Main.PhoneSocketPort);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				Main.connectedToPhone = true;
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}

}
