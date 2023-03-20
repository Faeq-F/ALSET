import java.io.IOException;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			String message;
			while (true) {
				if (Main.connectedToPhone) {
					//while ((message = Main.BTConnection.in.readLine()) != null)
				      //  lastMessage = message;
					//if (message != null && !(message.contains("null"))) {
						message = Main.in.readLine();
						if (message == null || message.contains("null")) {Main.connectedToPhone = false; continue;}
						if (message == "") message = "no_track_found";
						//lastMessage = message;
						Main.messageFromPhone = message;
						System.out.println("m: "+Main.messageFromPhone);
					//}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
