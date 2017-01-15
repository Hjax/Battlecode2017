package ecoBot;

import battlecode.common.*;

public class LumberJack extends Bot {
	public static void Start(RobotController RobCon) throws Exception{
		Bot.Init(RobCon);
		
	    System.out.println("I'm a lumberjack!");

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

	    	startTurn();
	    	
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {

	            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
	            RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

	            if(robots.length > 0 && !rc.hasAttacked()) {
	                // Use strike() to hit all nearby robots!
	                rc.strike();
	            } else {
	                // No close robots, so search for robots within sight radius
	                robots = rc.senseNearbyRobots(-1,enemy);

	                // If there is a robot, move towards it
	                if(robots.length > 0) {
	                    MapLocation myLocation = rc.getLocation();
	                    MapLocation enemyLocation = robots[0].getLocation();
	                    Direction toEnemy = myLocation.directionTo(enemyLocation);

	                    Utilities.tryMove(toEnemy);
	                } else {
	                	// dodge
	                    Utilities.tryMove(neo());
	                }
	            }

	            endTurn();

	        } catch (Exception e) {
	            System.out.println("Lumberjack Exception");
	            e.printStackTrace();
	        }
	    }
	}
}
