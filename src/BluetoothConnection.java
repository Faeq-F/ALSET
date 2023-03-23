import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import lejos.robotics.subsumption.Behavior;

public class BluetoothConnection implements Behavior{
	
	private final static String PhoneIP = "10.0.1.2";
	private final static int PhoneSocketPort = 1234;
	private static boolean connectedToPhone = false;
	private static String messageFromPhone;
	private static Socket clientSocket;
	private static BufferedReader in;
	private boolean _suppressed = false;
	
	@Override
	public boolean takeControl() {
		return !connectedToPhone;
	}

	@Override
	public void action() {
		_suppressed = false;
		if (!_suppressed){
			try {
				clientSocket = new Socket(PhoneIP, PhoneSocketPort);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				connectedToPhone = true;
			} catch(Exception e){
				System.out.println("failed to establish connection to phone - trying again");
			}
		}
	}

	@Override
	public void suppress() {
		_suppressed = true;
	}
	
	public String readLine() throws IOException {
		return in.readLine();
	}
	
	public void close() throws IOException {
		in.close();
		clientSocket.close();
	}
	
	public void setConnectedToPhone(boolean b){
		connectedToPhone = b;
	}
	
	public boolean isConnectedToPhone(){
		return connectedToPhone;
	}
	
	public void setMessageFromPhone(String message){
		messageFromPhone = message;
	}
	
	public String getMessageFromPhone(){
		return messageFromPhone;
	}

}
