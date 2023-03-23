import java.io.IOException;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			String message;
			while (true) {
				if (Main.getBTconnection().isConnectedToPhone()) {
					message = Main.getBTconnection().readLine();
					if (message == null || message.contains("null")){
						Main.getBTconnection().setConnectedToPhone(false);
						continue;
					} else if (message == "") // increased reliability
						message = "no_track_found";
					Main.getBTconnection().setMessageFromPhone(message);
				}
			}
		} catch (IOException e) {
			System.out.println("couldn't read message");
		}
	}
}
