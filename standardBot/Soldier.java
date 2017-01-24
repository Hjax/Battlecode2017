package standardBot;

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
            				Utilities.moveTo(Utilities.magnitudePressureDodge(bullets, robots[0].getLocation()));
            			}
            			else
            			{
            				Utilities.moveTo(Utilities.magnitudePressureDodge(bullets));
            			}
            			
            		}
            	else if (rc.getRoundNum() > 45 || Globals.getStrat() == 0)
        		{
        			Utilities.tryMove(neo());
        		}
            	
                

                
                
                // If there are some...
            	for (int i = 0; i < robots.length; i++){
                    if (robots[i].getType() != RobotType.ARCHON || rc.getRoundNum() > 500) {
                        MapLocation target = robots[i].location;
                        // And we have enough bullets, and haven't attacked yet this turn...;
                    	if (rc.getLocation().isWithinDistance(target, rc.getType().bodyRadius + 5.5f))
                    	{
                    		if (rc.canFirePentadShot() && !Utilities.willHitAlly(target)) {
                    			rc.firePentadShot(rc.getLocation().directionTo(target));
                    			break;
                    		}
                    	}
                        if (rc.canFireSingleShot() && !Utilities.willHitAlly(target)) 
                        {
                            // ...Then fire a bullet in the direction of the enemy.
                            rc.fireSingleShot(rc.getLocation().directionTo(target));
                            break;
                        }
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
