import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;

public class BluetoothInfo extends Thread{
	
	BluetoothInfo(){}
	
	public void run(){
		BTConnector connector = new BTConnector();
		NXTConnection conn = connector.waitForConnection(0, NXTConnection.RAW);
        InputStream IS = conn.openInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(IS), 1);
		while (true){
			//get information from phone
			try {
				movement.setMessageFromPhone(br.readLine());
	        } catch (IOException e) {
	            e.printStackTrace(System.out);
	        }
		}
	}
}
