package ecoBot;

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
		int build[] = new int[5];
		build[0] = 0;
		build[1] = 0;
		build[2] = 0;
		build[3] = 0;
		build[4] = 0;
		
		int buildLength = 4;
		
		int buildIndex = 0;
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	startTurn();
        	
        	if (rc.getRoundNum() - builtOn < 18)
        	{
        		System.out.println(rc.getRoundNum());
        		try {
					Utilities.tryMove(neo());
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        	else
        	{
        	

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
            					plantTree();
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
        	}
        	endTurn();
        }
	}
	
	private static void plantTree()
	{
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canPlantTree(angle) && turnCount++ < 90)
		{
			angle = angle.rotateLeftDegrees(4);
		}
		try {
			if (rc.canPlantTree(angle) && rc.getTeamVictoryPoints() < 900)
			{
				rc.plantTree(angle);
			}
			else waterTrees();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void waterTrees()
	{
		TreeInfo[] trees = rc.senseNearbyTrees(2);
		if (trees.length == 0)
			{return;}
		else
		{
			TreeInfo bestTree = trees[0];
			for (int treeCount = 0; treeCount < trees.length; treeCount++)
			{
				if (bestTree.getTeam() != rc.getTeam() || (trees[treeCount].getTeam() == rc.getTeam() && trees[treeCount].health < bestTree.health))
				{bestTree = trees[treeCount];}
			}
			try {
				if (rc.getTeam() == bestTree.getTeam())
						{rc.water(bestTree.ID);}
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void trainUnit(RobotType unit)
	{
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canBuildRobot(unit, angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			rc.buildRobot(unit, angle);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	
	
	
	
}
