public class TouchThread extends Thread{
	
	TouchThread(){}
	
	public void run(){
		while (true) {
			Main.spTouch.fetchSample(Main.touched,0);
		}
	}
	
}
