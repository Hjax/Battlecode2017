package standardBot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
        System.out.println("I'm an archon!");
        boolean tryBuild = false;
        
        // define build order
     	// 0 = gardener
     	// 1 = trainer
     	int build[] = new int[11];
     	build[0] = 0;
     	build[1] = 0;
     	build[2] = 1;
     	build[3] = 0;
     	build[4] = 0;
     	build[5] = 0;
     	build[6] = 1;
     	build[7] = 0;
     	build[8] = 1;
     	build[9] = 1;
     	build[10] = 1;
     	
     	int buildLength = 10;
     	int buildIndex = 0;
     	
     	boolean pause = false;
     	
     	try {
			Globals.initEdges();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
            	
            	
                // dodge
            	// pause for one turn after building
            	if (!pause)
            		{Utilities.tryMove(neo());}
            	else pause = false;

                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // build gardeners at reasonable times
                int round = rc.getRoundNum();
                if ((round == 1 && isFirst) || round == 100 || (round > 100 && round % 60 == 0))
                	{tryBuild = true;}
             
                if (tryBuild && rc.getTeamBullets() > 120) 
                {
                	trainGardener();
                    tryBuild = false;
                }
                



            } catch (Exception e) {
            	System.out.println("Archon Exception");
            	e.printStackTrace();
            	}
            endTurn();
        }
	}
	
	private static void trainGardener()
	{
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canHireGardener(angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			if (rc.canHireGardener(angle))
			{
				System.out.println("built gardener: " + angle.getAngleDegrees());
				System.out.println("(" + rc.getLocation().x + ", " + rc.getLocation().y + ")");
				rc.hireGardener(angle);
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void trainTrainer()
	{
		Direction angle = new Direction(0).rotateLeftDegrees(2.0f);
		int turnCount = 0;
		while (!rc.canHireGardener(angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			if (rc.canHireGardener(angle))
			{
				System.out.println("built trainer: " + angle.getAngleDegrees());
				rc.hireGardener(angle);
				
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
