package first_bot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon){
		Bot.Init(RobCon);
        System.out.println("I'm an archon!");
        boolean tryBuild = false;

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();

                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // build gardeners at reasonable times
                int round = rc.getRoundNum();
                if (round == 1 || round == 100 || (round > 100 && round % 20 == 0))
                	{tryBuild = true;}
                	
                if (tryBuild && rc.getTeamBullets() > 100) 
                {
                    trainGardener();
                    tryBuild = false;
                }

             // dodge
                Utilities.tryMove(neo());

                endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	
	private static void trainGardener()
	{
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canHireGardener(angle) && turnCount++ < 361)
		{
			angle = angle.rotateRightDegrees(1);
		}
		try {
			rc.hireGardener(angle);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
