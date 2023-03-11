import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			Socket clientSocket = new Socket(Main.PhoneIP, Main.PhoneSocketPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true) {
				Main.setMessageFromPhone(in.readLine());
				System.out.println(Main.getMessageFromPhone());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//testing method
	public static void main(final String[] array) {
		final BluetoothInfo bluetoothInfo = new BluetoothInfo();
        new ExitThread().start();
        bluetoothInfo.run();
    }
}
