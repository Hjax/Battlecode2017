package newMemory;

import battlecode.common.*;

public class LumberJack extends Bot {
	public static void Start(RobotController RobCon) throws Exception{
		
	    System.out.println("I'm a lumberjack!");

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

	    	startTurn();
	    	
        	int start = Clock.getBytecodeNum();
        	OrderManager.checkCreateOrderCheap();
        	System.out.print("Order checking used: ");
        	System.out.println(Clock.getBytecodeNum() - start);
        	
	    	
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	        	TreeInfo enemyTrees[] = rc.senseNearbyTrees(-1, enemy);
	        	RobotInfo gardeners[] = rc.senseNearbyRobots(-1, ally);
	        	
	        	TreeInfo neutralTrees[];
	        	
	        	neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
	        	

	        	
	        	if (enemyTrees.length > 0)
	        	{
	        		Utilities.moveTo(Utilities.melee(enemyTrees[0].getLocation(), 1 + enemyTrees[0].radius));
	        		if (rc.canChop(enemyTrees[0].ID))
	        			{rc.chop(enemyTrees[0].ID);}
	        	}
	        	else if (neutralTrees.length > 0)
	        	{
	        		Utilities.moveTo(Utilities.melee(neutralTrees[0].getLocation(), 1 + neutralTrees[0].radius));
	        		neutralTrees = rc.senseNearbyTrees(2, Team.NEUTRAL);
	        		System.out.println("try to chop neutral tree");
	        		if (neutralTrees.length != 0) {
		        		rc.setIndicatorDot(neutralTrees[0].getLocation(), 0, 0, 0);
		        		if (rc.canChop(neutralTrees[0].ID))
	        				{rc.chop(neutralTrees[0].ID);}
	        		}
	        	}
	        	else Utilities.tryMove(neo());

	           //make sure there are no allies in strike range
	        	RobotInfo[] allies = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, ally);
	        	if (allies.length == 0 && rc.canStrike())
	        	{
	        		 rc.strike();
	        	}

	        } catch (Exception e) {
	            System.out.println("Lumberjack Exception");
	            e.printStackTrace();

	        }
	        endTurn();
	    }
	}
}
