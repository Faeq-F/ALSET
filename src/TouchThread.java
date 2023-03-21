public class TouchThread extends Thread{
	
	public boolean pause = false;
	
	TouchThread(){}
	
	public void run(){
		while (true) {
			Main.spTouch.fetchSample(Main.touched,0);
			if (Main.touched[0] == 1.0 && (!pause)) {
				movement.stop();
				pause = true;
			} else if (Main.touched[0] == 1.0 && pause)
				pause = false;
			else if (pause)
				movement.stop();
		}
	}
}
