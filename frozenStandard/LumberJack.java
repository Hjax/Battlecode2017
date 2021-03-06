package frozenStandard;

import battlecode.common.*;

public class LumberJack extends Bot {
	public static void Start(RobotController RobCon) throws Exception{
		
	    Debug.debug_print("Starting lumberjack code");

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

	    	startTurn();
	    	
        	Debug.debug_bytecode_start();
        	OrderManager.checkCreateOrderCheap();
        	Debug.debug_bytecode_end("create order");
	    	
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	        	TreeInfo enemyTrees[] = rc.senseNearbyTrees(-1, enemy);
	        	
	        	TreeInfo neutralTrees[];
	        	
	        	neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
	        	

	        	
	        	if (enemyTrees.length > 0)
	        	{
	        		Utilities.moveTo(Utilities.melee(enemyTrees[0].getLocation(), 1 + enemyTrees[0].radius));
	        		if (rc.canChop(enemyTrees[0].ID))
	        			{rc.chop(enemyTrees[0].ID);}
	        	}
	        	
	        	if (neutralTrees.length > 0 && !rc.hasMoved())
	        	{
	        		Utilities.moveTo(Utilities.melee(neutralTrees[0].getLocation(), 1 + neutralTrees[0].radius));
	        		neutralTrees = rc.senseNearbyTrees(2, Team.NEUTRAL);
	        		Debug.debug_print("try to chop neutral tree");
	        		if (neutralTrees.length != 0) {
		        		rc.setIndicatorDot(neutralTrees[0].getLocation(), 0, 0, 0);
		        		if (rc.canChop(neutralTrees[0].ID))
	        				{rc.chop(neutralTrees[0].ID);}
	        		}
	        	}
	        	else Utilities.tryMove(neo());

	           //make sure there are no allies in strike range
	        	RobotInfo[] allies = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, ally);
	        	TreeInfo[] allyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, ally);
	        	RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
	        	if (allies.length == 0 && rc.canStrike())
	        	{
	        		if (allyTrees.length == 0 || enemies.length > 0){
	        			rc.strike();
	        		}
	        	}

	        } catch (Exception e) {
	            Debug.debug_print("Lumberjack Exception");
	            e.printStackTrace();

	        }
	        endTurn();
	    }
	}
}
