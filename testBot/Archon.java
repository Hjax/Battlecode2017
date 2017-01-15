package testBot;

import battlecode.common.*;

public class Archon extends Bot
{
	public static void Start(RobotController RobCon)
	{
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
     	
     	try {
     		rc.move(new Direction((float) Math.PI * 7 / 4)); 
     		
			trainTrainer();
			System.out.println(new Direction(rc.getLocation(), rc.senseNearbyRobots()[0].getLocation()));
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
     	
     	
     	

        // The code you want your robot to perform every round should be in this loop
        while (true)
        {

           Clock.yield();
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
