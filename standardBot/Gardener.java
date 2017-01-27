package standardBot;

import battlecode.common.*;

public class Gardener extends Bot
{
	static int buildIndex = 0;
	public static void Start(RobotController RobCon) throws Exception{
		
		Debug.debug_print("I'm a gardener!");
		
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
		int build[] = new int[9];
		
		
		
		// 0 = greedy/standard
		// 1 = soldier aggro into fast tank
		
		if (Globals.getStrat() == 1)
		{
			build[0] = 1;
			build[1] = 1;
			build[2] = 1;
			build[3] = 0;
			build[4] = 1;
			build[5] = 1;
			build[6] = 0;
			build[7] = 2;
			build[8] = 0;
		}
		else
		{
			build[0] = 1;
			build[1] = 0;
			build[2] = 0;
			build[3] = 0;
			build[4] = 4;
			build[5] = 0;
			build[6] = 0;
			build[7] = 0;
			build[8] = 0;
		}
		
		
		
		int buildLength = 8;
		int openerIndex = 0;
		
		MapLocation roost = null;
		boolean settled = false;
		
		boolean dynamicLumberjack = false;
		
		
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	
        	startTurn();
        	if (settled)
        	{
        		rc.setIndicatorLine(rc.getLocation(), roost, 100, 0, 0);
        	}
        	
        	
        	Debug.debug_print("start turn");
        	
        	
        	// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
    		try 
    		{
    			
        	
    			//dodge bullets if needed
    			BulletInfo bullets[] = rc.senseNearbyBullets(4);
    			MapLocation dodgeTo = rc.getLocation();
    			for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
    			{
    				dodgeTo = Utilities.dodgeBullet(bullets[bulletCount]);
    				if (!dodgeTo.equals(rc.getLocation()) && !rc.hasMoved())
    				{
    					Debug.debug_print("dodge bullet");
    					Utilities.moveTo(dodgeTo);
    				}
    			}
        	
    			if (!rc.hasMoved())
    			{
    				//run from enemies if near
    				RobotInfo enemies[] = rc.senseNearbyRobots(5, enemy);
    				dodgeTo = rc.getLocation();
            		if (enemies.length > 0)
            		{
            			Debug.debug_print("run from enemy");
            			dodgeTo = rc.getLocation().add(enemies[0].getLocation().directionTo(rc.getLocation()), 1.0f);
            			Utilities.moveTo(dodgeTo);
            		}
    			}
        	
        	
    			//if start of game, settle immediately. open with a scout
    			if (openerIndex == 0 && rc.getRoundNum() < 45 && rc.hasMoved() == false && rc.isBuildReady())
					{
    					settled = true;
    					if (roost == null)
    					{
    						roost = rc.getLocation().add(neo(), 1.0f);
    					}
    					
    					if (Globals.getStrat() == 0)
    					{
    						plantSpacedTree(roost);
    						buildIndex = 0;
    						openerIndex++;
    					}
    					else
    					{
    						openerIndex = -1;
    					}
    					
    				}
    			if (openerIndex == 1 && Globals.getStrat() == 0 && rc.isBuildReady())
				{
					Utilities.trainUnit(RobotType.SOLDIER);
					Debug.debug_print("trying to build a soldier");
					if (!rc.isBuildReady())
					{
						build[2] = 3;
						openerIndex++;
					}
					else
					{
						Debug.debug_print("failed to build a soldier");
					}
					
				}
    			
    			//find a place to settle
    			if (rc.getRoundNum() - builtOn < 23 && rc.hasMoved() == false && settled == false)
    			{
    				try {
    					Debug.debug_print("find a place to roost");
    					Utilities.tryMove(neo());
    				} catch (GameActionException e) {
    					e.printStackTrace();
    				}	
    			}	
    			else if (settled == true && rc.getLocation().distanceTo(roost) > 1.5f && rc.hasMoved() == false)
    			{
    				//return to roost if scared away
    				Debug.debug_print("return to roost");
    				Debug.debug_print("(" + roost.x + ", " + roost.y + ")");
    				Utilities.moveTo(roost);
    			}

    			if (rc.senseNearbyRobots(4, enemy).length > 0 && rc.getTeamBullets() >= 100)
				{
					Utilities.trainUnit(RobotType.SOLDIER);
				}
    			
    			//dynamic lumberjacks
    			if (dynamicLumberjack || (settled && rc.getRoundNum() > 4 && Globals.getStrat() == 0 && Bot.rand.nextDouble() * (1 + Globals.getUnitCount(UnitType.LUMBERJACK)) < Utilities.getDensity() - 0.4))
    			{
    				Debug.debug_print("dynamically building lumberjack");
    				dynamicLumberjack = true;
    				if (rc.isBuildReady() && rc.getTeamBullets() >= 100)
    				{
    					Utilities.trainUnit(RobotType.LUMBERJACK);
    					dynamicLumberjack = false;
    				}
    				
    			}
    				
    			
        		// execute build order if possible
    			if (openerIndex != 1 && !dynamicLumberjack)
    			{
    				Debug.debug_print("BUILD ORDER: " + buildIndex);
            		switch(build[buildIndex])
            		{
                		case 0:
                		{
                			if (rc.isBuildReady() && rc.getTeamBullets() > 50)
                			{
                				if (!settled)
                					{roost = rc.getLocation();}
                				if (plantSpacedTree(roost))
                				{
                					settled = true;	
                				}
                				
                    			if (buildIndex > buildLength)
                    				{buildIndex = 8;}
                				                					
                			}
                			break;
                		}
                		case 1:
                		{
                			if (rc.isBuildReady() && rc.getTeamBullets() > 100)
                			{
                				if (Globals.getUnitCount(UnitType.GARDENER) < 3)
                				{
                					Utilities.trainUnit(RobotType.SOLDIER);
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
                				Utilities.trainUnit(RobotType.TANK);
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
                				Debug.debug_print("building scout");
                				Utilities.trainUnit(RobotType.SCOUT);
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
                				Utilities.trainUnit(RobotType.LUMBERJACK);
                				if (!rc.isBuildReady())
                				{
                					buildIndex++;
                				}
                				
                				if (buildIndex > buildLength)
                					{buildIndex = 0;}
                			}
                			break;
                		}
            		}
    			}
    			

        	}

        	catch (Exception e) {
        		Debug.debug_print("Gardener Exception");
        		e.printStackTrace();
        	}
    		Debug.debug_print("end turn");
    		if (rc.canWater() && settled)
    		{
    			Debug.debug_print("watering");
    			waterTrees(roost);
    		}
    		else
    		{
    			Debug.debug_print("can't water");
    		}
        	endTurn();
        }
	}
	
	private static boolean plantSpacedTree(MapLocation roost) throws GameActionException
	{
		Debug.debug_bytecode_start();
		Direction angle = new Direction(0);
		int turnCount = 0;
		Debug.debug_print("trying to plant");
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(angle, 3.0f), 1.05f) || (Globals.getUnitCount(UnitType.GARDENER) < 2 && (rc.senseNearbyTrees(roost.add(angle, 3.0f), 1.05f, ally).length + rc.senseNearbyTrees(roost.add(angle, 3.0f), 2.5f, Team.NEUTRAL).length > 0 || !rc.onTheMap(roost.add(angle, 3.0f), 2.5f))) || !rc.onTheMap(roost.add(angle, 3.0f), 1.05f)) && turnCount++ < 8)
		{
			rc.setIndicatorDot(roost.add(angle, 3.0f), 155, 155, 155);
			
			angle = angle.rotateLeftDegrees(45);
		}
		try 
		{
			if (turnCount < 8)
			{
				if (!rc.hasMoved())
				{
					Debug.debug_print("moving to plant");
					Utilities.moveTo((roost.add(angle, 2.0f)));
				}
				else
				{
					Debug.debug_print("already moved, can't plant");
				}
				if (rc.getLocation().distanceTo(roost.add(angle, 3.0f)) <= 2.0f)
				{
					buildIndex++;
					Debug.debug_print("Planting");
					rc.plantTree(rc.getLocation().directionTo(roost.add(angle, 3.0f)));
				}
				
				return true;
			}
			else
			{
				Debug.debug_print("can't plant");
				buildIndex++;
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		Debug.debug_bytecode_end("planting");
		return false;
	}
	
	private static void waterTrees(MapLocation roost) throws GameActionException
	{
		Debug.debug_bytecode_start();
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
		Debug.debug_bytecode_end("watering");
	}

}
