package frozenStandard;

import battlecode.common.*;

public class Gardener extends Bot
{
	static int buildIndex = 0;
	public static MapLocation roost = null;
	static boolean isStuck = true;
	public static void Start(RobotController RobCon) throws Exception{
		
		Debug.debug_print("I'm a gardener!");
		
		//for age purposes
		int builtOn = rc.getRoundNum();
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	
        	startTurn();
        	if (roost != null)
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
        	
        	
    			
    			
    			
    			
    			
    			if (roost == null || BuildManager.treesPlanted == 0)
    			{
    				TreeInfo[] alliedTrees = rc.senseNearbyTrees(6, ally);
    				if (alliedTrees.length == 0)
    				{
    					roost = rc.getLocation();
    					isStuck = false;
    				}
    				
    			}
    			
    			if (true) {
    				BuildManager.executeBuild();
    			}
    			
    			//find a place to settle
    			if (!rc.hasMoved() && (roost == null || BuildManager.treesPlanted == 0))
    			{
    				try {
    					Debug.debug_print("find a place to roost");
    					Utilities.tryMove(neo());
    				} catch (GameActionException e) {
    					e.printStackTrace();
    				}	
    			}	
    			

    			if (!rc.hasMoved() && roost != null && rc.getLocation().distanceTo(roost) > 0.1f)
    			{
    				//return to roost if scared away
    				Debug.debug_print("return to roost");
    				Debug.debug_print("(" + roost.x + ", " + roost.y + ")");
    				Utilities.moveTo(roost);
        		}
    			
    			
    			
    			
    		}
        	catch (Exception e) {
        		Debug.debug_print("Gardener Exception");
        		e.printStackTrace();
        	}
    		
    		
    		
    		Debug.debug_print("end turn");
    		
    		if (rc.canWater() && roost != null)
    		{
    			Debug.debug_print("watering");
    			Utilities.waterTrees(roost);
    		}
    		else
    		{
    			Debug.debug_print("can't water");
    		}
    		
    		if (isStuck) 
    		{
    			Globals.updateStuckGardeners();
    		}
        	endTurn();
        }
	}

}
