package frozenStandard;

import battlecode.common.*;

public class Soldier extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException
	{
		
		System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
            	
            	if (Globals.getOrderCount() == 0) {
                	Debug.debug_bytecode_start();
                	OrderManager.checkCreateOrderCheap();
                	Debug.debug_bytecode_end("create orders");
            	}

                MapLocation target = null;
                Direction angle = null;
                // If there are some...
                Debug.debug_print("starting aim");
            	for (int i = 0; i < enemiesMaxRange.length; i++){
                    if (enemiesMaxRange[i].getType() != RobotType.ARCHON || rc.getRoundNum() > 250) {
                        target = enemiesMaxRange[i].location;
                        // And we have enough bullets, and haven't attacked yet this turn...;
                		if (!Utilities.willHitAlly(target)) 
                		{
                			angle = rc.getLocation().directionTo(target);
                			angle = angle.rotateRightRads((float) (Math.asin(0.2 / rc.getLocation().distanceTo(target))));
                			target = rc.getLocation().add(angle, rc.getLocation().distanceTo(target));
                			rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
                			break;
                		}
                		else
            			{
            				System.out.println("not shooting to avoid hitting ally");
            			}
                    }
            	}
            	
            	MapLocation moveDesire = rc.getLocation();
            	// dodge
            	MapLocation hug = rc.getLocation();
            	if (enemiesMaxRange.length > 0)
    			{
            		Debug.debug_print("enemy in vision");
    				for (int i = 0; i < enemiesMaxRange.length; i++)
    				{
    					if (enemiesMaxRange[i].getType() != RobotType.ARCHON)
    					{
    						if (enemiesMaxRange[i].getType() == RobotType.SOLDIER || enemiesMaxRange[i].getType() == RobotType.LUMBERJACK)
    						{
    							hug = Utilities.melee(enemiesMaxRange[i].getLocation(), 4f);
    						}
    						else
    						{
    							hug = Utilities.melee(enemiesMaxRange[i].getLocation(), 2);
    						}
    						break;
    					}
    				}
    				if (hug.equals(rc.getLocation()))
    				{
    					hug = enemiesMaxRange[0].getLocation();
    				}
    			}
            	else
            	{
            		hug = rc.getLocation().add(neo());
            	}
            	BulletInfo[] bullets = rc.senseNearbyBullets(6);
            	if (bullets.length > 0 && rc.getType() != RobotType.TANK)
            	{
            		moveDesire = Utilities.naivePressureDodge(bullets, hug);		
            	}
            	else if (rc.getRoundNum() > 45 || Globals.getStrat() == BuildManager.STANDARD)
        		{
            		moveDesire = hug;     		
        		}
            	
            	float angleDiff = 0;
            	if (angle != null)
            	{
            		angleDiff = Math.abs(rc.getLocation().directionTo(moveDesire).degreesBetween(angle));
            	}
            	Debug.debug_print("angleDiff: " + angleDiff);
            	if (angleDiff < 90)
            	{
            		Debug.debug_print("move before shoot");
            		Utilities.moveTo(moveDesire);
                	shoot(target, angle); 	
            	}
            	else
            	{
            		Debug.debug_print("shoot before move");
            		shoot(target, angle); 
            		Utilities.moveTo(moveDesire);
                		
            	}

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
	
	private static void shoot(MapLocation target, Direction angle) throws GameActionException
	{
		if (angle != null && target != null)
    	{
    		if (rc.canFirePentadShot() && (rc.getType() == RobotType.TANK || rc.getTreeCount() > 4 || rc.getTeamBullets() > 50 || rc.getLocation().isWithinDistance(target, rc.getType().bodyRadius + 5.5f)))
			{
				if (rc.getTeamBullets() < 8)
				{
					rc.fireTriadShot(angle);
				}
				else
				{
					rc.firePentadShot(angle);
				}
    			
    		} else 
    		{
            	// ...Then fire a bullet in the direction of the enemy.
    			if (rc.canFireSingleShot())
    			{
    				rc.fireSingleShot(angle);
    			}
    		}
    	}
	}
}
