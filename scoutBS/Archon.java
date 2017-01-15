package scoutBS;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		Bot.Init(RobCon);
        System.out.println("I'm an archon!");
        boolean tryBuild = false;
        
        // define build order
     	// 0 = gardener
     	// 1 = trainer
     	int build[] = new int[11];
     	build[0] = 0;
     	build[1] = 0;
     	build[2] = 0;
     	build[3] = 0;
     	build[4] = 0;
     	build[5] = 0;
     	build[6] = 1;
     	build[7] = 0;
     	build[8] = 0;
     	build[9] = 0;
     	build[10] = 0;
     	
     	int buildLength = 10;
     	int buildIndex = 0;
     	
     	boolean pause = false;
     	
     	try {
			Memory.initEdges();
		} catch (GameActionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
     	int archonNum = 0;
     	MapLocation archonStarts[] = rc.getInitialArchonLocations(ally);
     	for (int archonCount = 0; archonCount < archonStarts.length; archonCount++)
     	{
     		if (rc.getLocation().equals(archonStarts[archonCount]))
     			{archonNum = archonCount;}
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
                if ((round == 1 && archonNum == 0) || round == 100 || (round > 100 && round % 100 == 0))
                	{tryBuild = true;}
             
                if (tryBuild && rc.getTeamBullets() > 120) 
                {
                	switch(build[buildIndex++])
                	{
                		case 0:
                		{
                			if (buildIndex > buildLength)
        						{buildIndex = 0;}
                			trainGardener();
                			pause = true;
                			break;
                		}
                		case 1:
                		{
                			if (buildIndex > buildLength)
        						{buildIndex = 0;}
                			System.out.println("trainTrainer");
                			trainTrainer();
                			pause = true;
                			break;
                		}
                	}
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
