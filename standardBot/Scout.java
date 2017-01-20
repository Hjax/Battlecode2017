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
            	
            	TreeInfo trees[] = rc.senseNearbyTrees();
            	if (trees.length > 0){
            		for (TreeInfo tree: trees){
            			if (tree.getContainedBullets() >= 10){
            				Utilities.moveTo(tree.location);
            				break;
            			}
            		}
            	}

            	RobotInfo enemies[] = rc.senseNearbyRobots(-1, enemy);
            	RobotInfo gardener = rc.senseRobot(rc.getID());
            	for (int countBot = 0; countBot < enemies.length; countBot++)
            	{
            		if (enemies[countBot].getType() == RobotType.GARDENER)
            		{
            			gardener = enemies[countBot];
            			break;
            		}
            	}
            	
            	BulletInfo bullets[] = rc.senseNearbyBullets(4);
            	
            	if (bullets.length > 0){
            		System.out.println("PRESSURE DODGE");
                	MapLocation dodgeTo = Utilities.magnitudePressureDodge(bullets);
                	Utilities.moveTo(dodgeTo);
                	System.out.println("PRESSURE DODGE DONE");
                	
            	}

            	
            	else if (gardener.getType() == RobotType.GARDENER)
            	{
            		if (gardener.location.distanceTo(rc.getLocation()) < 4){
            			// we are already in melee range, find the closest enemy, and move to the side of the gardener
            			System.out.println("Being annoying");
            			RobotInfo defense =  rc.senseRobot(rc.getID());
    	            	for (int countBot = 0; countBot < enemies.length; countBot++)
    	            	{
    	            		if (enemies[countBot].getType() == RobotType.SOLDIER || enemies[countBot].getType() == RobotType.SCOUT || enemies[countBot].getType() == RobotType.TANK || enemies[countBot].getType() == RobotType.LUMBERJACK)
    	            		{
    	            			defense = enemies[countBot];
    	            			break;
    	            		}
    	            	}
    	            	MapLocation target = gardener.getLocation();
    	            	target = target.add(defense.location.directionTo(gardener.location), 2.01f);
    	            	rc.setIndicatorDot(target, 255, 255, 255);
    	            	Utilities.moveTo(target);

    	            	
            		} else {
                		System.out.println("MELEE");
                		Utilities.moveTo((Utilities.melee(gardener.getLocation(), 2)));
            		}
            	}
            	else 
            	{
            		if (! rc.hasMoved()){
                		if (rc.getRoundNum() < 3000)
                		{
                			
                			Utilities.tryMove(goal);
                		}
                		else
                		{
                			Utilities.tryMove(neo());
                		}
            		}
            	}
            	rc.setIndicatorDot(rc.getLocation().add(goal, 4.0f), 0, 0, 255);
            	
            	System.out.println(goal.getAngleDegrees());
            	System.out.println(rc.onTheMap(rc.getLocation().add(goal, 4.0f), 1.0f) == false);
            	if (rc.onTheMap(rc.getLocation().add(goal, 4.0f), 1.0f) == false)
            	{
            		System.out.println("setting new goal");
            		goal = goal.rotateLeftRads((float) (Math.random() * 2 * Math.PI));
            	}
            	System.out.println(goal.getAngleDegrees());
            	
            	

                // See if there are any nearby enemy robots
            	RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
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
