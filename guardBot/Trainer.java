package guardBot;

import battlecode.common.*;

public class Trainer extends Bot{
	public static void Start(RobotController RobCon) throws Exception
	{
		Bot.Init(RobCon);
		
		System.out.println("I'm a trainer!");
		
		//for age purposes
		int builtOn = rc.getRoundNum();
		
		//map edges
		
		
		// define build order
		// 0 = tree
		// 1 = soldier
		// 2 = tank
		// 3 = scout
		// 4 = lumberjack
		// 5 = gardener
		// 6 = archon
		// 7 = neutral tree
		int build[] = new int[14];
		build[0] = 4;
		build[1] = 1;
		build[2] = 1;
		build[3] = 1;
		build[4] = 1;
		build[5] = 4;
		build[6] = 1;
		build[7] = 1;
		build[8] = 1;
		build[9] = 1;
		build[10] = 1;
		build[11] = 1;
		build[12] = 1;
		build[13] = 1;
		
		
		
		int buildLength = 13;
		
		int buildIndex = 0;
		
		behaviorType = 1;
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	startTurn();
        	
			Utilities.tryMove(neo());


        	// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
        	try 
        	{

        		// execute build order if possible
        		switch(build[buildIndex])
        		{
            		case 1:
            		{
            			if (rc.getRoundNum() < 1)
            			{
            				if (rc.isBuildReady() && rc.getTeamBullets() > 100)
            				{
            					trainUnit(RobotType.SOLDIER);
            					buildIndex++;
            					if (buildIndex > buildLength)
            					{buildIndex = 0;}
            				}
            			}
            			else 
            			{
            				if (rc.isBuildReady() && rc.getTeamBullets() > 300)
                			{
                				trainUnit(RobotType.TANK);
                				buildIndex++;
                				if (buildIndex > buildLength)
                				{buildIndex = 0;}
                			}
            			}
            			break;
            		}
            		case 2:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 300)
            			{
            				trainUnit(RobotType.TANK);
            				buildIndex++;
            				if (buildIndex > buildLength)
            				{buildIndex = 0;}
            			}
            			break;
            		}
            		case 3:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 80)
            			{
            				trainUnit(RobotType.SCOUT);
            				buildIndex++;
            				if (buildIndex > buildLength)
            				{buildIndex = 0;}
            			}
            			break;
            		}
            		case 4:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 100)
            			{
            				trainUnit(RobotType.LUMBERJACK);
            				buildIndex++;
            				if (buildIndex > buildLength)
            				{buildIndex = 0;}
            			}
            			break;
            		}
        		}

        	}

        	catch (Exception e) {
        		System.out.println("Gardener Exception");
        		e.printStackTrace();
        	}
        	endTurn();
        }
	}
	
	private static void trainUnit(RobotType unit)
	{
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canBuildRobot(unit, angle) && turnCount++ < 60)
		{
			angle = angle.rotateRightDegrees(6);
		}
		try {
			if (rc.canBuildRobot(unit,  angle))
				{rc.buildRobot(unit, angle);}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	
	
	
	
}
