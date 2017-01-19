package sprint;

import battlecode.common.*;
import sprint.Utilities;

public class Guard extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
		System.out.println("I'm an guard!");
		RobotInfo gardenerToGuard = rc.senseRobot(rc.getID());

		try
		{
			gardenerToGuard = rc.senseNearbyRobots(2, ally)[0];
		} catch (Exception e) {
            System.out.println("Soldier Exception");
            e.printStackTrace();
        }
		
		int guardedMem = Memory.findAllyInMemory(gardenerToGuard.location);
		

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	System.out.println("start turn");
            	
            	if (Memory.readAlly(guardedMem).toInt() == 0 || memory_loc == guardedMem)
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
            			guardedMem = Memory.findAllyInMemory(gardenerToGuard.location);
            		}
            		else Utilities.tryMove(neo());
            	}
            	else
            	{
            		MapLocation target = Memory.readAlly(guardedMem).location;
            		RobotInfo enemies[] = rc.senseNearbyRobots(-1, enemy);
                	// if no enemies, stay near gardener
            		TreeInfo trees[] = rc.senseNearbyTrees(target, 3, ally);
            		// if no enemies and a tree, sit in the tree
                	if (enemies.length == 0)
                	{
                		if (trees.length == 0 || true)
                		{
                			Utilities.moveTo(target.add(target.directionTo(rc.getLocation()).rotateLeftRads((float) (Math.random() - 0.5)), 5.01f));
                		}
                		else
                		{
                			Utilities.moveTo(target.add(target.directionTo(trees[0].getLocation()), 4.01f));
                		}
                		
                		
                	}
                	else
                	{
                		
                		Utilities.moveTo(target.add(target.directionTo(enemies[0].getLocation()), 2.01f));
                	}
                	rc.setIndicatorLine(rc.getLocation(), target, 255, 255, 255);
            	}
            	
                

            	// See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
                // If there are some...
                if (robots.length > 0) {
                    RobotInfo enemyTarget = robots[0];
                    if (!(enemyTarget.getType() == RobotType.ARCHON && rc.getRoundNum() < 300))
                    {
                    	// And we have enough bullets, and haven't attacked yet this turn...;
                    	if (rc.getLocation().isWithinDistance(enemyTarget.getLocation(), 4.0f)){
                    		if (rc.canFirePentadShot()) {
                    			rc.firePentadShot(rc.getLocation().directionTo(enemyTarget.getLocation()));
                    		}
                    	}
                        if (rc.canFireSingleShot()) {
                            // ...Then fire a bullet in the direction of the enemy.
                            rc.fireSingleShot(rc.getLocation().directionTo(enemyTarget.getLocation()));
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
