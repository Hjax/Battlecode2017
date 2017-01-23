package soldierCheeseBot;

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
		int build[] = new int[8];
		build[0] = 1;
		build[1] = 1;
		build[2] = 1;
		build[3] = 1;
		build[4] = 1;
		build[5] = 3;
		build[6] = 1;
		build[7] = 0;
		
		int buildLength = 7;
		
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
    					plantSpacedTree(roost);
    				}
    			if (rc.getRoundNum() > 5 && rc.getRoundNum()< 15)
				{
					trainUnit(RobotType.SOLDIER);
					//build[0] = 3;
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
    			else if (settled == true && rc.getLocation().distanceTo(roost) > 1f && rc.hasMoved() == false)
    			{
    				//return to roost if scared away
    				System.out.println("return to roost");
    				System.out.println("(" + roost.x + ", " + roost.y + ")");
    				Utilities.moveTo(roost);
    			}


        		// execute build order if possible
    			if (rc.getRoundNum() > 15)
    			{
    				System.out.println("build order");
            		switch(build[buildIndex])
            		{
                		case 0:
                		{
                			if (rc.isBuildReady() && rc.getTeamBullets() > 50)
                			{
                				if (rc.senseNearbyRobots(4, enemy).length == 0)
                				{
                					if (!settled)
                						{roost = rc.getLocation();}
                					if (Globals.getGardenerCount() < 3)
                					{
                						if (plantSpacedTree(roost))
                    					{
                    						settled = true;
                    						
                    							
                    					}
                					}
                					else if (plantSpacedTree(roost))
                					{
                						settled = true;
                						
                							
                					}
                					System.out.println("BUILD ORDER: " + buildIndex);
                					buildIndex++;
                    				if (buildIndex > buildLength)
                    					{buildIndex = 7;}
                				}
                				else if (rc.getTeamBullets() >= 100)
                				{
                					trainUnit(RobotType.SOLDIER);
                				}
                					
                			}
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
    			

        	}

        	catch (Exception e) {
        		System.out.println("Gardener Exception");
        		e.printStackTrace();
        	}
    		System.out.println("end turn");
    		if (rc.canWater())
    		{
    			System.out.println("watering");
    			waterTrees(roost);
    		}
    		else
    		{
    			System.out.println("can't water");
    		}
        	endTurn();
        }
	}
	
	private static boolean plantSpacedTree(MapLocation roost) throws GameActionException
	{
		int bytes = Clock.getBytecodeNum();
		Direction angle = new Direction(0);
		int turnCount = 0;
		System.out.println("trying to plant");
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(angle, 3.0f), 1.05f) || (Globals.getGardenerCount() < 2 && (rc.senseNearbyTrees(roost.add(angle, 3.0f), 1.05f, ally).length + rc.senseNearbyTrees(roost.add(angle, 3.0f), 3.0f, Team.NEUTRAL).length > 0 || !rc.onTheMap(roost.add(angle, 3.0f), 3.0f))) || !rc.onTheMap(roost.add(angle, 3.0f), 1.0f)) && turnCount++ < 24)
		{
			rc.setIndicatorDot(roost.add(angle, 3.0f), 155, 155, 155);
			
			angle = angle.rotateLeftDegrees(15);
		}
		try {
			if (!rc.isCircleOccupied(roost.add(angle, 3.0f), 1.0f) && rc.hasMoved() == false && rc.onTheMap(roost.add(angle, 3.0f), 1.0f))
			{
				Utilities.moveTo((roost.add(angle, 2.0f)));
				if (rc.getLocation().distanceTo(roost.add(angle, 3.0f)) <= 2.0f)
				{
					rc.plantTree(rc.getLocation().directionTo(roost.add(angle, 3.0f)));
				}
				
				return true;
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("planting takes: " + (Clock.getBytecodeNum() - bytes));
		return false;
	}
	
	private static boolean plantTree() throws GameActionException
	{
		int bytes = Clock.getBytecodeNum();
		Direction angle = new Direction(0);
		int turnCount = 0;
		while (!rc.canPlantTree(angle) && turnCount++ < 60 && !(Globals.getGardenerCount() > 3 || rc.isCircleOccupiedExceptByThisRobot(rc.getLocation().add(angle, 2.01f), 2.9f)))
		{
			angle = angle.rotateLeftDegrees(30);
		}
		try {
			if (rc.canPlantTree(angle))
			{
				rc.plantTree(angle);
				return true;
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("planting takes: " + (Clock.getBytecodeNum() - bytes));
		return false;
	}
	
	private static void waterTrees(MapLocation roost) throws GameActionException
	{
		int bytes = Clock.getBytecodeNum();
		TreeInfo[] trees = rc.senseNearbyTrees(2.0f, ally);
		if (trees.length > 0)
		{
			TreeInfo bestTree = trees[0];
			for (int treeCount = 1; treeCount < trees.length; treeCount++)
			{
				if (trees[treeCount].health < bestTree.health)
				{bestTree = trees[treeCount];}
			}
			try {
				rc.setIndicatorDot(bestTree.getLocation(), 255, 0, 0);
				rc.water(bestTree.ID);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		if (!rc.hasMoved())
		{
			trees = rc.senseNearbyTrees(roost, 3, ally);
			if (trees.length > 0)
			{
				TreeInfo bestTree = trees[0];
				for (int treeCount = 1; treeCount < trees.length; treeCount++)
				{
					if (trees[treeCount].health < bestTree.health)
					{bestTree = trees[treeCount];}
				}
				rc.setIndicatorDot(bestTree.getLocation(), 0, 255, 0);
				Utilities.moveTo(Utilities.melee(bestTree.getLocation(), 2.01f));
			}
				
		}
		System.out.println("watering takes: " + (Clock.getBytecodeNum() - bytes));
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
