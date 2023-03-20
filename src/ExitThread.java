import lejos.hardware.Button;

import java.io.IOException;

import lejos.hardware.Battery;

public class ExitThread extends Thread{
	
	ExitThread(){}
	
	@Override
	public void run(){
		while (true){
			if(Button.ESCAPE.isDown() || (Battery.getVoltage() < Main.LOW_BATTERY)) {
				Main.UltraSonicSensor.close();
				Main.TouchSensor.close();
				try {
					Main.clientSocket.close();
				} catch (IOException e) {
					System.out.println("tried closing a socket that is already closed or does not exist");
				}
				System.exit(0);
			}
		}
	}
}