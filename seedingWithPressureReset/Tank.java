package seedingWithPressureReset;

import battlecode.common.*;

public class Tank extends Bot {
	public static void Start(RobotController RobCon) throws GameActionException{

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	startTurn();

                // If there are some...
                if (enemiesMaxRange.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(enemiesMaxRange[0].location));
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
}
