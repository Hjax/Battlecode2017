package standardBot;

import battlecode.common.*;

public class LumberJack extends Bot {
	public static void Start(RobotController RobCon) throws Exception{
		Bot.Init(RobCon);
		
	    System.out.println("I'm a lumberjack!");

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

	    	startTurn();
	    	
	        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        try {
	        	TreeInfo enemyTrees[] = rc.senseNearbyTrees(-1, enemy);
	        	TreeInfo neutralTrees[] = rc.senseNearbyTrees(-1, Team.NEUTRAL);
	        	TreeInfo bestTree;
	        	
	        	if (enemyTrees.length > 0)
	        	{
	        		// find closest enemy tree
	        		bestTree = enemyTrees[0];
	        		for (int treeCount = 0; treeCount < enemyTrees.length; treeCount++)
	        		{
	        			if (rc.getLocation().distanceTo(enemyTrees[treeCount].getLocation()) < rc.getLocation().distanceTo(bestTree.getLocation()))
	        				{bestTree = enemyTrees[treeCount];}
	        		}
	        		rc.setIndicatorDot(bestTree.getLocation(), 0, 250, 0);
	        		Utilities.moveTo(Utilities.melee(bestTree.getLocation(), 1 + bestTree.radius));
	        		if (rc.canChop(bestTree.ID))
	        			{rc.chop(bestTree.ID);}
	        	}
	        	else if (neutralTrees.length > 0)
	        	{
	        		// find closest neutral tree
	        		bestTree = neutralTrees[0];
	        		for (int treeCount = 0; treeCount < neutralTrees.length; treeCount++)
	        		{
	        			if (rc.getLocation().distanceTo(neutralTrees[treeCount].getLocation()) < rc.getLocation().distanceTo(bestTree.getLocation()))
	        				{bestTree = neutralTrees[treeCount];}
	        		}
	        		rc.setIndicatorDot(bestTree.getLocation(), 0, 250, 0);
	        		Utilities.moveTo(Utilities.melee(bestTree.getLocation(), 1 + bestTree.radius));
	        		if (rc.canChop(bestTree.ID))
        			{rc.chop(bestTree.ID);}
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
