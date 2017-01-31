package standardBot;

import battlecode.common.*;

public class Scout extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
		System.out.println("I'm a scout!");
		
		Direction goal = rc.getLocation().directionTo(rc.getInitialArchonLocations(enemy)[0]);
		System.out.println(goal.getAngleDegrees());

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
            	MapLocation moveDesire = rc.getLocation();
            	Debug.debug_bytecode_start();
            	OrderManager.checkCreateOrderCheap();
            	Debug.debug_bytecode_end("create order");
            	
            	TreeInfo trees[] = rc.senseNearbyTrees();
            	if (trees.length > 0){
            		for (TreeInfo tree: trees){
            			if (tree.getContainedBullets() >= 10){
            				moveDesire = tree.location;
            				break;
            			}
            		}
            	}

            	RobotInfo gardener = rc.senseRobot(rc.getID());
            	for (int countBot = 0; countBot < enemiesMaxRange.length; countBot++)
            	{
            		if (enemiesMaxRange[countBot].getType() == RobotType.GARDENER)
            		{
            			gardener = enemiesMaxRange[countBot];
            			break;
            		}
            	}
            	
            	BulletInfo bullets[] = rc.senseNearbyBullets(8);
            	
            	if (bullets.length > 0)
            	{
            		Utilities.moveTo(Utilities.naivePressureDodge(bullets, rc.getLocation().add(neo())));
            	}
            	else if (moveDesire.equals(rc.getLocation()) == false)
            	{
            		Utilities.moveTo(moveDesire);
            	}
            	else if (gardener.getType() == RobotType.GARDENER)
            	{
            		Utilities.moveTo(rc.getLocation().add(neo()));
            	}
            	else 
            	{
            		if (! rc.hasMoved())
            		{
            			Utilities.tryMove(neo());
            		}
            	}
            	rc.setIndicatorDot(rc.getLocation().add(goal, 4.0f), 0, 0, 255);
            	
            	System.out.println(goal.getAngleDegrees());
            	System.out.println(rc.onTheMap(rc.getLocation().add(goal, 4.0f), 1.0f) == false);
            	if (rc.onTheMap(rc.getLocation().add(goal, 4.0f), 1.0f) == false)
            	{
            		System.out.println("setting new goal");
            		goal = goal.rotateLeftRads((float) (rand.nextDouble() * 2 * Math.PI));
            	}
            	System.out.println(goal.getAngleDegrees());
            	
            	

                // See if there are any nearby enemy robots
            	RobotInfo[] robots = rc.senseNearbyRobots(2.5f, enemy);
                // If there are some...
                if (robots.length > 0) 
                {
                    RobotInfo target = robots[0];
                    // And we have enough bullets, and haven't attacked yet this turn...;
                    if (rc.canFireSingleShot() && (target.getType() == RobotType.GARDENER || rc.getRoundNum() > 300 || target.getType() == RobotType.SCOUT)) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(target.getLocation()));
                    }
                }

            } catch (Exception e) 
            {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }

            endTurn();
        }
    }
	
}
