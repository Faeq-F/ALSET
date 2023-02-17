import lejos.robotics.subsumption.Behavior;

public class ObjectTraversal implements Behavior{

	@Override
	public boolean takeControl() {
		return false;
		
	}

	@Override
	public void action() {
		
	}

	@Override
	public void suppress() {
		
	}
	
	public static void main(String[] args) {
		// Turn 90 degrees on the stop
		
		// Also turn distance detection so that it points
		// towards wall.
		
		// Reset tacho count
		
		// while loop, keep going until does not detect wall beside it
		
		// keep going forwards for a little bit
		
		// assign tacho count to a var
		
		// turn -90, do NOT change distance sensor position
		
		// keep going until wall is not detected on the side
		// as obstruction may be a box.
		
		// turn -90 again
		
		// go forward the amount of tacho count we assigned before
		
		// then rotate 90. In theory, we should be back where robot 
		// was when it was behind the box/obstruction.

	}

}
