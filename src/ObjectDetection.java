public class ObjectDetection extends Thread {

	ObjectDetection(){}
	
	//public boolean stop_thread = false;
	
	public void run() {
		while (true){
			Main.spUS.fetchSample(Main.distance, 0) ;
			Main.setDistanceFromObject(Main.distance[0]);
			//if (stop_thread) break;
		}
	}
}