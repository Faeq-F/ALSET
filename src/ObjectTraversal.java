
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

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
	
	// NOTE: 
	//	- To make robot rotate ~90 degrees on the spot, make it rotate by 200 amounts.
	//	- The ultrasonic sensor motor's turn is clockwise relative to the top of the robot,
	//	  meaning that 90 makes it turn to its right, 0 to the front and so on. 
	//	- REMEMBER, ultrasonic's motor is set 0 as when PROGRAM STARTS, there is no absolute.	
	
	final static float WALLLENFROMROBOT = 0.126f ;
	
	final static int DELAYBY = 1000 ;
	
	
	
	public static void main(String[] args) {		
		// ULTRASONIC SENSOR PARTS
		// assign tacho to vars
		int tach_start, tach_end ;
		
		// turn robot to the left.
		movement.turnLeft() ;
		
		// Also turn distance detection so that it points
		// towards wall.
		movement.SensRight() ;
		
		// 		KEEP GOING UNTIL WALL IS NO LONGER DETECTED
		// Get starting tacho count, this will be used later
		tach_start = movement.getTachoCount() ;
		
		// keep going until no more obstruction detected
		movement.forward() ;
		
		float current_distance ;
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLLENFROMROBOT) {
				break;
			}
		}
		
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
		
		// assign tacho count to a var
		tach_end = movement.getTachoCount() ;
		
		// stop robot
		movement.stop() ;
		
		
		// turn -90, do NOT change distance sensor position
		movement.turnRight() ;
		
				
		// keep going forwards for a little bit before starting to 
		// detect wall as when robot turns, sensor is a bit behind
		// and don't want robot to turn prematurely.
		movement.forward() ;
		Delay.msDelay(DELAYBY) ;
		
		// keep going until wall is not detected on the side
		// as obstruction may be a box.
		while(true) {
			current_distance = Main.getDistanceFromObject();
			if (current_distance >= WALLLENFROMROBOT) {
				break;
			}	
		}
		
		// keep going forwards for a little bit then stop
		Delay.msDelay(DELAYBY) ;
				
		// stop robot
		movement.stop() ;
		
		// turn right again
		movement.turnRight() ;
		
		// go forward the amount of tacho count robot travelled
		// after turning left but before it turn right.
		float distance_travelled = tach_end - tach_start ;
		movement.forward((int) distance_travelled) ;
		
		// then rotate 90. In theory, we should be back where robot 
		// was when it was behind the box/obstruction.
		movement.SensLeft() ;

	}

}
