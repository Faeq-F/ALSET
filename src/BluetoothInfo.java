import java.io.IOException;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			String message;
			while (true) {
				if (Main.BTConnection.getConnectedToPhone()) {
					message = Main.BTConnection.readLine();
					if (message == null || message.contains("null")){
						Main.BTConnection.setConnectedToPhone(false);
						continue;
					} else if (message == "")
						message = "no_track_found";
					Main.BTConnection.setMessageFromPhone(message);
				}
			}
		} catch (IOException e) {
			System.out.println("couldn't read message");
		}
	}
}
