package standardBot;

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
            	hug = rc.getLocation().add(neo());
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
            		if (moveDesire.equals(rc.getLocation()))
            		{
            			angleDiff = 0;
            		}
            		else
            		{
            			angleDiff = Math.abs(rc.getLocation().directionTo(moveDesire).degreesBetween(angle));
            		}
            		
            	}
            	Debug.debug_print("angleDiff: " + angleDiff);
            	
            	if (angle != null && target != null) {
            		Memory.addOrder(new Order(1, target, rc.getRoundNum() + 2, -1));
            	}
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
            	
                if (!rc.hasAttacked() && OrderManager.shouldGroundFire() && rc.getLocation().distanceTo(OrderManager.getTarget()) < 15) {
                	if (!Utilities.willHitAlly(rc.getLocation().add(rc.getLocation().directionTo(OrderManager.getTarget()), rc.getType().sensorRadius))) {
                		shoot(OrderManager.getTarget(), rc.getLocation().directionTo(OrderManager.getTarget()));
                	}
                }

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }

            endTurn();
        }
    }
	
	private static void shoot(MapLocation target, Direction angle) throws Exception
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
