package guardBot;

import battlecode.common.*;

public class Guard extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		Bot.Init(RobCon);
		
		System.out.println("I'm an guard!");
		RobotInfo gardenerToGuard = rc.senseNearbyRobots(2, ally)[0];
		

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	
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
                

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(2, enemy);
                
                // If there are some...
                if (robots.length > 0) {
                    rc.strike();
                }




            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
}
