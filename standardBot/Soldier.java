package standardBot;

import battlecode.common.*;

public class Soldier extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
		System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	startTurn();
                
            	// dodge
            	BulletInfo[] bullets = rc.senseNearbyBullets(6);
            	if (bullets.length > 0 && rc.getType() != RobotType.TANK)
            		{
            			if (enemiesMaxRange.length > 0)
            			{
            				Utilities.moveTo(Utilities.naivePressureDodge(bullets, enemiesMaxRange[0].getLocation()));
            			}
            			else
            			{
            				Utilities.moveTo(Utilities.naivePressureDodge(bullets, rc.getLocation().add(neo(), rc.getType().strideRadius)));
            			}
            			
            		}
            	else if (rc.getRoundNum() > 45 || Globals.getStrat() == BuildManager.STANDARD)
        		{
        			Utilities.tryMove(neo());
        		}

                
                
                // If there are some...
            	for (int i = 0; i < enemiesMaxRange.length; i++){
                    if (enemiesMaxRange[i].getType() != RobotType.ARCHON || rc.getRoundNum() > 250) {
                        MapLocation target = enemiesMaxRange[i].location;
                        // And we have enough bullets, and haven't attacked yet this turn...;
                		if (!Utilities.willHitAlly(target)) 
                		{
                			Direction angle = rc.getLocation().directionTo(target);
                			angle = angle.rotateRightRads((float) (Math.asin(0.2 / rc.getLocation().distanceTo(target))));
                			target = rc.getLocation().add(angle, rc.getLocation().distanceTo(target));
                			rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
                			if (rc.canFirePentadShot() && (rc.getType() == RobotType.TANK || rc.getLocation().isWithinDistance(target, rc.getType().bodyRadius + 5.5f)))
                			{
                				if (rc.getTeamBullets() < 8)
                				{
                					rc.fireTriadShot(angle);
                				}
                				else
                				{
                					rc.firePentadShot(angle);
                				}
                    			
                    			break;
                    		} else {
                            	// ...Then fire a bullet in the direction of the enemy.
                    			if (rc.canFireSingleShot())
                    			{
                    				rc.fireSingleShot(angle);
                    			}
                            	
                            	break;
                    		}
                		}
                		else
            			{
            				System.out.println("not shooting to avoid hitting ally");
            			}
                    }
            	}
            	
            	if (!rc.hasAttacked())
            	{
            		System.out.println("did not fire this turn");
            	}

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
            endTurn();
        }
    }
}
