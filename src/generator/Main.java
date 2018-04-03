package generator;

import java.io.File;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class Main {
	
	

	static String header = "E:/";
	
	public static void main(String[] args) {
		
		System.out.println("Starting generation...");
		
		Waypoint waypoints1[] = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(30, 0, 0)
		};
		
		Waypoint waypoints2[] = new Waypoint[] {
				
				new Waypoint(0,0,0),
				new Waypoint(108, -50, 0),
		};
		
		Waypoint waypoints3[] = new Waypoint[] {
				new Waypoint(0,0,0),
				new Waypoint(75, -50, 0),
		};
		
		generateTrajectory(waypoints2, "CenterSwitchRight1", false);
		generateTrajectory(waypoints3, "CenterSwitchRight2", true);
		generateTrajectory(waypoints1, "CenterSwitchRight3" , false);
		generateTrajectory(waypoints1, "CenterSwitchRight4" , true);
		generateTrajectory(waypoints3, "CenterSwitchRight5", false);		
		
		Waypoint waypoints4[] = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(30, 0, 0)
		};
		
		Waypoint waypoints5[] = new Waypoint[] {
				
				new Waypoint(0,0,0),
				new Waypoint(108, 50, 0),
		};
		
		Waypoint waypoints6[] = new Waypoint[] {
				new Waypoint(0,0,0),
				new Waypoint(75, 50, 0),
		};
		
		generateTrajectory(waypoints5, "CenterSwitchLeft1", false);
		generateTrajectory(waypoints6, "CenterSwitchLeft2", true);
		generateTrajectory(waypoints4, "CenterSwitchLeft3" , false);
		generateTrajectory(waypoints4, "CenterSwitchLeft4" , true);
		generateTrajectory(waypoints6, "CenterSwitchLeft5", false);
		
		Waypoint waypoint7[] = new Waypoint[] {
				new Waypoint(0,0,0),
				new Waypoint(100, 0, 0),
		};
		
		generateTrajectory(waypoint7, "LeftScaleLeft1", false);
		
		generatePivotTurn("right", -90);
		generatePivotTurn("left", 90);
		generatePivotTurn("allLeft", 180);
		generatePivotTurn("allRight", -180);
		
		System.out.println("All Generation ended");
	}
	
	private static void generateTrajectory(Waypoint waypoints[], String name, boolean isReversed) {
		
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, .01, 100, 80, 160);

		
		System.out.println("Generating Path: " + name);
		Trajectory trajectory = Pathfinder.generate(waypoints, config);
		TankModifier modifier = new TankModifier(trajectory).modify(26);
		
		Trajectory left;
		Trajectory right;
		
		if(!isReversed) {
			left = modifier.getLeftTrajectory();
			right = modifier.getRightTrajectory();
		}
		else {
			right = modifier.getLeftTrajectory();
			left = modifier.getRightTrajectory();
			for(int i = 0; i < right.segments.length; i++) {
				right.segments[i].position *= -1;
				right.segments[i].velocity *= -1;
			}
			for(int i = 0; i < left.segments.length; i++) {
				left.segments[i].position *= -1;
				left.segments[i].velocity *= -1;
			}
		}
		
		File leftFile = new File(header + name + "Left");
		File rightFile = new File(header + name + "Right");
		
		Pathfinder.writeToFile(leftFile, left);
		Pathfinder.writeToFile(rightFile, right);
		
	}
	
	private static void generatePivotTurn(String name, double angle){
		
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, .01, 100, 80, 160);

		
		System.out.println("Generating Path: " + name);
		
		
		
		Waypoint[] waypoints = new Waypoint[]{
			new Waypoint(0, 0, 0),
			new Waypoint(.5 * Pathfinder.d2r(angle) * 26, 0, 0)
		};
		
		Trajectory trajectory = Pathfinder.generate(waypoints, config);
		TankModifier modifier = new TankModifier(trajectory).modify(26);
		
		Trajectory left;
		Trajectory right;
		
		if(angle > 0) {
			left = modifier.getLeftTrajectory();
			right = modifier.getRightTrajectory();
			for(int i = 0; i < left.segments.length; i++) {
				left.segments[i].heading = 2 * left.segments[i].position / 26;
				right.segments[i].heading = 2 * right.segments[i].position / 26;
				left.segments[i].position *= -1;
				left.segments[i].velocity *= -1;
			}
		}
		else {
			right = modifier.getLeftTrajectory();
			left = modifier.getRightTrajectory();
			for(int i = 0; i < right.segments.length; i++) {
				left.segments[i].heading = -2 * left.segments[i].position / 26;
				right.segments[i].heading = -2 * right.segments[i].position / 26;
				right.segments[i].position *= -1;
				right.segments[i].velocity *= -1;
			}
		}
		
		File leftFile = new File(header + name + "Left");
		File rightFile = new File(header + name + "Right");
		
		Pathfinder.writeToFile(leftFile, left);
		Pathfinder.writeToFile(rightFile, right);
		
	}

}
