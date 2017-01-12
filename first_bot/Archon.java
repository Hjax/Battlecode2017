package first_bot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon){
		Bot.Init(RobCon);
        System.out.println("I'm an archon!");
        boolean tryBuild = false;
        
        // define build order
     	// 0 = gardener
     	// 1 = trainer
     	int build[] = new int[11];
     	build[0] = 0;
     	build[1] = 1;
     	build[2] = 0;
     	build[3] = 0;
     	build[4] = 0;
     	build[5] = 0;
     	build[6] = 0;
     	build[7] = 0;
     	build[8] = 0;
     	build[9] = 0;
     	build[10] = 0;
     	
     	int buildLength = 10;
     	int buildIndex = 0;
     	
     	

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
                // dodge
            	System.out.println("move");
                Utilities.tryMove(neo());

                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // build gardeners at reasonable times
                int round = rc.getRoundNum();
                if (round == 1 || round == 100 || (round > 100 && round % 40 == 0))
                	{tryBuild = true;}
             
                if (tryBuild && rc.getTeamBullets() > 100) 
                {
                	switch(build[buildIndex++])
                	{
                		case 0:
                		{
                			if (buildIndex > buildLength)
        						{buildIndex = 0;}
                			trainGardener();
                			break;
                		}
                		case 1:
                		{
                			if (buildIndex > buildLength)
        						{buildIndex = 0;}
                			System.out.println("trainTrainer");
                			trainTrainer();
                			break;
                		}
                	}
                    tryBuild = false;
                }
                
                try {
                	System.out.println("end start");
    				endTurn();
    				System.out.println("end end");
    			} catch (GameActionException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}



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
		while (!rc.canHireGardener(angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			if (rc.canHireGardener(angle))
			{
				System.out.println("built gardener");
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
				System.out.println("built trainer");
				rc.hireGardener(angle);
				
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
