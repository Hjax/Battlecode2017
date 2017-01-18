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
     	
     	
     	try {
			Globals.initEdges();
			Globals.updateEdges();
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
            	Utilities.tryMove(neo());

                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // build gardeners at reasonable times
                int round = rc.getRoundNum();
                if ((round == 1 && isFirst) || (Globals.getGardenerCount() - 2 < (Globals.getSoldierCount() + Globals.getTankCount() + Globals.getScoutCount() + Globals.getLumberjackCount())/6 && rc.getRoundNum() > 30) || Globals.getTrainerCount() < Math.floor((Globals.getGardenerCount())/2) || rc.getTeamBullets() > 600)
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
	
	private static void trainGardener() throws GameActionException
	{
		Direction angle = new Direction(0);
		float leftDist, rightDist, topDist, bottomDist;
		leftDist = 1000;
		rightDist = 1000;
		topDist = 1000;
		bottomDist = 1000;
		
		
		
		if (Globals.getLeftEdge() != -1)
		{
			leftDist = rc.getLocation().x - Globals.getLeftEdge();
		}
		if (Globals.getRightEdge() != -1)
		{
			rightDist = Globals.getRightEdge() - rc.getLocation().x;
		}
		if (Globals.getBottomEdge() != -1)
		{
			bottomDist =  rc.getLocation().y - Globals.getBottomEdge();
		}
		if (Globals.getTopEdge() != -1)
		{
			topDist = Globals.getTopEdge() - rc.getLocation().y;
		}
		
		System.out.println("left = " + leftDist);
		System.out.println("right = " + rightDist);
		System.out.println("top = " + topDist);
		System.out.println("bottom = " + bottomDist);
		
		
		if (leftDist < rightDist)
		{
			if (bottomDist < topDist)
				{
					if (leftDist < bottomDist)
					{
						System.out.println("a");
						angle = new Direction((float)Math.PI);
					}
					else
						{
							System.out.println("b");
							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (leftDist < topDist)
			{
				System.out.println("c");
				angle = new Direction((float)Math.PI);
			}
			else
				{
				System.out.println("d");
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		else
		{
			if (bottomDist < topDist)
				{
					if (rightDist < bottomDist)
					{
						System.out.println("e");
						angle = new Direction(0);
					}
					else
						{
							System.out.println("f");
							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (rightDist < topDist)
			{
				System.out.println("g");
				angle = new Direction(0);
			}
			else
				{
					System.out.println("h");
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		
		System.out.println("building at angle " + angle.getAngleDegrees());
			
			
			
		
		
		
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

}
