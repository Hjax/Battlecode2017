package standardBot;

import battlecode.common.*;
import standardBot.Utilities;

public class Guard extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		Bot.Init(RobCon);
		
		System.out.println("I'm an guard!");
		RobotInfo gardenerToGuard = rc.senseRobot(rc.getID());
		try
		{
			gardenerToGuard = rc.senseNearbyRobots(2, ally)[0];
		} catch (Exception e) {
            System.out.println("Soldier Exception");
            e.printStackTrace();
        }
		
		

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	System.out.println("start turn");
            	
            	{
            		System.out.println("gardener died");
            		gardenerToGuard = rc.senseRobot(rc.getID());
            	}
            	if (gardenerToGuard.getID() == rc.getID())
            	{
            		System.out.println("look for gardener");
            		RobotInfo allies[] = rc.senseNearbyRobots(-1, ally);
            		RobotInfo gardener = gardenerToGuard;
            		
            		for (int countBot = 0; countBot < allies.length; countBot++)
                	{
                		if (allies[countBot].getType() == RobotType.GARDENER)
                		{
                			System.out.println("found gardener");
                			gardener = allies[countBot];
                			break;
                		}
                	}
            		if (gardener.getID() != gardenerToGuard.getID())
            		{
            			System.out.println("set gardener");
            			gardenerToGuard = gardener;
            		}
            	}
            	else
            	{
            		RobotInfo enemies[] = rc.senseNearbyRobots(-1, enemy);
                	// if no enemies, stay near gardener
                	if (enemies.length == 0)
                	{
                		Utilities.moveTo(Utilities.melee(gardenerToGuard.getLocation(), 4.01f));
                	}
                	else
                	{
                		Utilities.moveTo(gardenerToGuard.getLocation().add(gardenerToGuard.getLocation().directionTo(enemies[0].getLocation()), 4.01f));
                	}
            	}
            	
                

            	// See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
                // If there are some...
                if (robots.length > 0) {
                    MapLocation target = robots[0].location;
                    // And we have enough bullets, and haven't attacked yet this turn...;
                	if (rc.getLocation().isWithinDistance(target, 5.5f)){
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
