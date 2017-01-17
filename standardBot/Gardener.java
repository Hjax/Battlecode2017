package standardBot;

import battlecode.common.*;

public class Gardener extends Bot{
	public static void Start(RobotController RobCon) throws Exception{
		
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
		int build[] = new int[7];
		build[0] = 1;
		build[1] = 0;
		build[2] = 0;
		build[3] = 4;
		build[4] = 0;
		build[5] = 0;
		build[6] = 0;
		
		int buildLength = 6;
		
		int buildIndex = 0;
		
		MapLocation roost = rc.getLocation();
		boolean settled = false;
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	
        	startTurn();
        	rc.setIndicatorLine(rc.getLocation(), roost, 100, 0, 0);
        	System.out.println("start turn");
        	rc.setIndicatorDot(rc.getLocation(), 100, 100, 0);
        	
        	// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
    		try 
    		{
        	
    			//dodge bullets if needed
    			BulletInfo bullets[] = rc.senseNearbyBullets(4);
    			MapLocation dodgeTo = rc.getLocation();
    			for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
    			{
    				dodgeTo = Utilities.dodgeBullet(bullets[bulletCount]);
    				if (dodgeTo.equals(rc.getLocation()) == false && rc.hasMoved() == false);
    				{
    					System.out.println("dodge bullet");
    					Utilities.moveTo(dodgeTo);
    				}
    			}
        	
    			if (rc.hasMoved() == false)
    			{
    				//run from enemies if near
    				RobotInfo enemies[] = rc.senseNearbyRobots(5, enemy);
    				dodgeTo = rc.getLocation();
            		if (enemies.length > 0)
            		{
            			System.out.println("run from enemy");
            			dodgeTo = rc.getLocation().add(enemies[0].getLocation().directionTo(rc.getLocation()), 1.0f);
            			Utilities.moveTo(dodgeTo);
            		}
    			}
        	
        	
    			//if start of game, settle immediately.open with a scout
    			if (rc.getRoundNum() < 5 && rc.hasMoved() == false && rc.isBuildReady())
					{
    					settled = true;
    					roost = rc.getLocation();
    					plantTree();
    				}
    			if (rc.getRoundNum() > 5 && rc.getRoundNum()< 15)
				{
					trainUnit(RobotType.SCOUT);
				}
        	
    			//find a place to settle
    			if (rc.getRoundNum() - builtOn < 23 && rc.hasMoved() == false && settled == false)
    			{
    				try {
    					System.out.println("find a place to roost");
    					Utilities.tryMove(neo());
    				} catch (GameActionException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}	
    			}	
    			else if (settled == true && rc.getLocation().equals(roost) == false && rc.hasMoved() == false)
    			{
    				//return to roost if scared away
    				System.out.println("return to roost");
    				System.out.println("(" + roost.x + ", " + roost.y + ")");
    				Utilities.moveTo(roost);
    			}


        		// execute build order if possible
    			System.out.println("build order");
        		switch(build[buildIndex])
        		{
            		case 0:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 50)
            			{
            				if (rc.senseNearbyBullets().length == 0 || true)
            				{
            					if (!settled)
            						{roost = rc.getLocation();}
            					if (Globals.getGardenerCount() < 3)
            					{
            						if (plantTree())
                					{
                						settled = true;
                							
                					}
            					}
            					else if (plantSpacedTree())
            					{
            						settled = true;
            							
            					}
            						
            					buildIndex++;
                				if (buildIndex > buildLength)
                					{buildIndex = 1;}
            				}
            				else if (rc.getTeamBullets() >= 100)
            				{
            					trainUnit(RobotType.SOLDIER);
            				}
            				else {waterTrees();}
            					
            			}
            			else 
            				{waterTrees();}
            			break;
            		}
            		case 1:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 100)
            			{
            				if (Globals.getGardenerCount() < 3)
            				{
            					trainUnit(RobotType.SOLDIER);
            				}
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
            		case 4:
            		{
            			if (rc.isBuildReady() && rc.getTeamBullets() > 100)
            			{
            				trainUnit(RobotType.LUMBERJACK);
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
    		System.out.println("end turn");
        	endTurn();
        }
	}
	
	private static boolean plantSpacedTree() throws GameActionException
	{
		int bytes = Clock.getBytecodeNum();
		Direction angle = new Direction(0);
		int turnCount = 0;
		System.out.println("trying to plant");
		while ((rc.isCircleOccupied(rc.getLocation().add(angle, 2.90f), 1.0f) || !rc.onTheMap(rc.getLocation().add(angle, 2.90f), 1.0f)) && turnCount++ < 15)
		{
			angle = angle.rotateLeftDegrees(45);
		}
		System.out.println(rc.hasMoved());
		System.out.println(!rc.isCircleOccupied(rc.getLocation().add(angle, 1.90f), 1.0f));
		try {
			if (!rc.isCircleOccupied(rc.getLocation().add(angle, 2.90f), 1.0f) && rc.hasMoved() == false && rc.onTheMap(rc.getLocation().add(angle, 2.90f), 1.0f))
			{
				Utilities.moveTo(Utilities.melee(rc.getLocation().add(angle, 2.90f), 2.0f));
				rc.plantTree(angle);
				return true;
			}
			else waterTrees();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("watering takes: " + (Clock.getBytecodeNum() - bytes));
		return false;
	}
	
	private static boolean plantTree()
	{
		int bytes = Clock.getBytecodeNum();
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canPlantTree(angle) && turnCount++ < 60)
		{
			angle = angle.rotateLeftDegrees(30);
		}
		try {
			if (rc.canPlantTree(angle))
			{
				rc.plantTree(angle);
				return true;
			}
			else waterTrees();
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
