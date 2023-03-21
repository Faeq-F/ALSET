import java.io.IOException;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			String message;
			while (true) {
				if (Main.connectedToPhone) {
					message = Main.in.readLine();
					if (message == null || message.contains("null")){
						Main.connectedToPhone = false;
						continue;
					} else if (message == "")
						message = "no_track_found";
					Main.messageFromPhone = message;
					System.out.println("recieved: " + Main.messageFromPhone);
				}
			}
		} catch (IOException e1) {
			System.out.println("could not read message from connected phone");
		}
	}
}
