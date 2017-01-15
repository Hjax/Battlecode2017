package standardBot;

import battlecode.common.*;

public class Scout extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		Bot.Init(RobCon);
		
		System.out.println("I'm a scout!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	
            	// look for edges of the map if unknown
            	if (Globals.getLeftEdge() == -1)
            		{Utilities.tryMove(new Direction((float)Math.PI));}
            	else if (Globals.getRightEdge() == -1)
        			{Utilities.tryMove(new Direction(0.0f));}
            	if (Globals.getTopEdge() == -1)
        			{Utilities.tryMove(new Direction((float)Math.PI / 2));}
            	else if (Globals.getBottomEdge() == -1)
        			{Utilities.tryMove(new Direction((float)Math.PI * 3 / 2));}
            	else 
            	{

            		// dodge
            		Utilities.tryMove(neo());
            	}
            		

                // See if there are any nearby enemy robots
            	RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                // If there are some...
                if (robots.length > 0) 
                {
                    MapLocation target = robots[0].location;
                    // And we have enough bullets, and haven't attacked yet this turn...;
                    if (rc.canFireSingleShot() && !Utilities.willHitAlly(rc.getLocation(), rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target))) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(target));
                    }
                }

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
}
