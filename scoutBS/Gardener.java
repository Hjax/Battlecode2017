package scoutBS;

import battlecode.common.*;

public class Gardener extends Bot{
	public static void Start(RobotController RobCon) throws Exception{
		Bot.Init(RobCon);
		
		System.out.println("I'm a gardener!");
		
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
		int build[] = new int[10];
		build[0] = 3;
		build[1] = 3;
		build[2] = 0;
		build[3] = 0;
		build[4] = 0;
		build[5] = 3;
		build[6] = 3;
		build[7] = 0;
		build[8] = 0;
		build[9] = 3;
		
		int buildLength = 9;
		
		int buildIndex = 0;
		
		boolean settled = false;
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	
        	startTurn();
        	
        	if (rc.getRoundNum() < 25)
				{trainUnit(RobotType.SCOUT);}
        	
        	if (rc.getRoundNum() - builtOn < 18 || settled == false)
        	{
        		try {
					Utilities.tryMove(neo());
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        	

        	// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
        	try 
        	{
        			

        		// execute build order if possible
        		switch(build[buildIndex])
        		{
            		case 0:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 50)
            			{
            				settled = plantTree();
            				buildIndex++;
            				if (buildIndex > buildLength)
            					{buildIndex = 0;}
            			}
            			else 
            				{waterTrees();}
            			break;
            		}
            		case 1:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 100)
            			{
            				trainUnit(RobotType.SOLDIER);
            				buildIndex++;
            				if (buildIndex > buildLength)
            						{buildIndex = 0;}
            			}
            			else waterTrees();
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
            			else waterTrees();
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
            			else waterTrees();
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
	
	private static boolean plantTree()
	{
		int bytes = Clock.getBytecodeNum();
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canPlantTree(angle) && turnCount++ < 60)
		{
			angle = angle.rotateLeftDegrees(6);
		}
		try {
			if (rc.canPlantTree(angle))
			{
				rc.plantTree(angle);
				return true;
			}
			else waterTrees();
			return false;
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("watering takes: " + (Clock.getBytecodeNum() - bytes));
		return false;
	}
	
	private static void waterTrees()
	{
		int bytes = Clock.getBytecodeNum();
		TreeInfo[] trees = rc.senseNearbyTrees(2, ally);
		if (trees.length == 0)
			{return;}
		else
		{
			TreeInfo bestTree = trees[0];
			for (int treeCount = 0; treeCount < trees.length; treeCount++)
			{
				if (trees[treeCount].health < bestTree.health)
				{bestTree = trees[treeCount];}
			}
			try {
				rc.water(bestTree.ID);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("planting takes: " + (Clock.getBytecodeNum() - bytes));
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
