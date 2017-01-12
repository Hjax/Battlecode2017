package first_bot;

import battlecode.common.*;

public class Gardener extends Bot{
	public static void Start(RobotController RobCon){
		Bot.Init(RobCon);

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	startTurn();

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);

                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                // Move randomly
                Utilities.tryMove(Utilities.randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                endTurn();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
	}
}
