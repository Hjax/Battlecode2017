package sprint;

import battlecode.common.*;

public class Soldier extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
		System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	
            	// dodge
                Utilities.tryMove(neo());

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
                // If there are some...
                if (robots.length > 0) {
                    MapLocation target = robots[0].location;
                    // And we have enough bullets, and haven't attacked yet this turn...;
                	if (rc.getLocation().isWithinDistance(target, 4.5f)){
                		if (rc.canFirePentadShot() && !Utilities.willHitAlly(rc.getLocation(), rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target))) {
                			rc.firePentadShot(rc.getLocation().directionTo(target));
                		}
                	}
                    if (rc.canFireSingleShot() && !Utilities.willHitAlly(rc.getLocation(), rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target))) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(target));
                    }
                }




            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
}
