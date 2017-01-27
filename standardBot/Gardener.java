package standardBot;

import battlecode.common.*;

public class Gardener extends Bot
{
	static int buildIndex = 0;
	static MapLocation roost = null;
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
        	
        	
    			//if start of game, settle immediately
    			if (rc.getRoundNum() < 45 && !rc.hasMoved() && rc.isBuildReady())
					{
    					if (roost == null)
    					{
    						roost = rc.getLocation().add(neo(), 1.0f);
    					}    					
    				}
    			
    			
    			
    			//find a place to settle
    			if (rc.getRoundNum() - builtOn < 23 && !rc.hasMoved() && roost == null)
    			{
    				try {
    					Debug.debug_print("find a place to roost");
    					Utilities.tryMove(neo());
    				} catch (GameActionException e) {
    					e.printStackTrace();
    				}	
    			}	
    			else if (roost != null && rc.getLocation().distanceTo(roost) > 1.5f && !rc.hasMoved())
    			{
    				//return to roost if scared away
    				Debug.debug_print("return to roost");
    				Debug.debug_print("(" + roost.x + ", " + roost.y + ")");
    				Utilities.moveTo(roost);
        		} else if (roost != null) {
    				BuildManager.executeBuild();
    			} else {
    				roost = rc.getLocation();
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
        	endTurn();
        }
	}

}
