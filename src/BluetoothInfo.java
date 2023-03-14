import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			while (true) {
				Main.setMessageFromPhone(Main.btconnect.in.readLine());
				System.out.println(Main.getMessageFromPhone());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
