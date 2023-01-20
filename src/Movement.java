

public class Movement {
	
	private static int Delay_for_1DEG_Turn = 1;
	private static int Delay_for_1CM_Forward = 1;
	
	public static void TurnLeft() {
		System.out.println(Delay_for_1DEG_Turn);
	}
	
	public static void TurnRight() {
		System.out.println(Delay_for_1DEG_Turn);
	}
	
	public static void MoveForward() {
		System.out.println(Delay_for_1CM_Forward);
	}

	public static void main(String[] args) {
		//test methods here
		MoveForward();
		TurnRight();
		TurnLeft();
	}

}
