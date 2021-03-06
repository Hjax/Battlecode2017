package soldierCheeseBot;

import battlecode.common.*;

public class Soldier extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
		System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	
            	// See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
            	// dodge
            	BulletInfo[] bullets = rc.senseNearbyBullets(4);
            	if (bullets.length > 0 && rc.getType() != RobotType.TANK)
            		{
            		if (robots.length > 0)
            		{
            			Utilities.moveTo(Utilities.magnitudePressureDodge(bullets, Utilities.melee(robots[0].getLocation(), 1 + robots[0].getType().bodyRadius)));
            		}
            		else
            		{
            			Utilities.moveTo(Utilities.magnitudePressureDodge(bullets));
            		}
            			
            		}
            	else if (rc.getRoundNum() > 45)
            		{
            			Utilities.tryMove(neo());
            		}
            	
                

                
                
                // If there are some...
                if (robots.length > 0 && (robots[0].getType() != RobotType.ARCHON || rc.getRoundNum() > 500)) {
                    MapLocation target = robots[0].location;
                    // And we have enough bullets, and haven't attacked yet this turn...;
                	if (rc.getLocation().isWithinDistance(target, rc.getType().bodyRadius + 4.5f)){
                		if (rc.canFirePentadShot() && !Utilities.willHitAlly(target)) {
                			rc.firePentadShot(rc.getLocation().directionTo(target));
                		}
                	}
                    if (rc.canFireSingleShot() && !Utilities.willHitAlly(target)) {
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
