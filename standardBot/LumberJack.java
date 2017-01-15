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
	        		if (rc.canChop(enemyTrees[0].ID))
	        			{rc.chop(enemyTrees[0].ID);}
	        	}
	        	else if (neutralTrees.length > 0)
	        	{
	        		if (rc.canChop(neutralTrees[0].ID))
        			{rc.chop(neutralTrees[0].ID);}
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
