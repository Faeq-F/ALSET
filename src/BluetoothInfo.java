import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		try {
			Socket clientSocket = new Socket("10.0.1.2", 1234);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true){
				System.out.println(in.readLine());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	}
	
	public static void main(final String[] array) {
        final BluetoothInfo bluetoothInfo = new BluetoothInfo();
        new ExitThread().start();
        bluetoothInfo.run();
    }
}
