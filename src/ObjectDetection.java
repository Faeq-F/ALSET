public class ObjectDetection extends Thread {

	ObjectDetection(){}
	
	public void run() {
		while (true){
			Main.spUS.fetchSample(Main.distance, 0) ;
			Main.setDistanceFromObject(Main.distance[0]);
		}
	}
}