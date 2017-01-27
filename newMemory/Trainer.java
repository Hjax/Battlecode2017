package newMemory;

import battlecode.common.*;

public class Trainer extends Bot{
	public static void Start(RobotController RobCon) throws Exception
	{
		
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
		build[0] = 1;
		build[1] = 1;
		build[2] = 1;
		build[3] = 4;
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
        	
        	Debug.debug_bytecode_start();
        	OrderManager.checkCreateOrderCheap();
        	Debug.debug_bytecode_end("create order");
        	
        	rc.setIndicatorDot(rc.getLocation(), 0, 100, 100);
        	
			Utilities.tryMove(neo());

			if (Globals.getUnitCount(UnitType.SOLDIER) + 3 * Globals.getUnitCount(UnitType.TANK) + Globals.getUnitCount(UnitType.LUMBERJACK) + Globals.getUnitCount(UnitType.SCOUT) < 150)
			{
				// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        	try 
	        	{
	        		
	        		if (Globals.getOrderCount() == 0 && Globals.getUnitCount(UnitType.SCOUT) < 4 && rand.nextDouble() < 0.25) {
	        			if (rc.isBuildReady() && rc.getTeamBullets() > RobotType.SCOUT.bulletCost) {
	        				Utilities.trainUnit(RobotType.SCOUT);
	        			}
	        		}

	        		// execute build order if possible
	        		switch(build[buildIndex])
	        		{
	            		case 1:
	            		{
	            			if (Globals.getUnitCount(UnitType.GARDENER) < 10)
	            			{
	            				if (rc.isBuildReady() && rc.getTeamBullets() > 100 && (Globals.getUnitCount(UnitType.SOLDIER) * 1 < rc.getTreeCount()))
	            				{
	            					Utilities.trainUnit(RobotType.SOLDIER);
	            					buildIndex++;
	            					if (buildIndex > buildLength)
	            					{buildIndex = 0;}
	            				}
	            			}
	            			else 
	            			{
	            				if (rc.isBuildReady() && rc.getTeamBullets() > 300)
	                			{
	                				Utilities.trainUnit(RobotType.TANK);
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
			}
        	
        	endTurn();
        }
	}
}
