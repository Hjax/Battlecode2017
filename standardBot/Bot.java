package standardBot;

import java.util.Random;
import battlecode.common.*;


public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
	public static MapLocation[] allyArchons;
	public static MapLocation[] enemyArchons;
	public static boolean isFirst = false;
	public static Random rand;
	public static MapLocation lastPosition;
	public static RobotInfo[] enemiesMaxRange = new RobotInfo[0];
	private static Direction goal;
	private static int goalDate;
	
    protected static void Init(RobotController RobCon) throws GameActionException{
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	allyArchons = rc.getInitialArchonLocations(ally);
    	enemyArchons = rc.getInitialArchonLocations(enemy);
    	lastPosition = rc.getLocation();
    	
    	rand = new Random(rc.getID());
    	goal = new Direction((float) rand.nextDouble() - 0.5f,(float) rand.nextDouble() - 0.5f);
    	goalDate = rc.getRoundNum();
    	
    	try {
    		Globals.updateCurrentUnitCount();
			Globals.updateEdges();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    protected static void startTurn() throws Exception 
    {
    	
    	if (Globals.getRoundNumber() != rc.getRoundNum()){
    		Globals.setRoundNumber(rc.getRoundNum());
    		Globals.clearUnitCounts();
    		Globals.clearStuckGardeners();
  
    		if (rc.getRoundNum() == 1)
    		{
    			Globals.initEdges();
    			Globals.updateEdges();
    		}
    		if (rc.getRoundNum() == 2) {
    			// we went first on the second round, add enemy archon spawns as orders
    			TreeInfo[] trees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
    			for (int i = 0; i < trees.length; i++) {
    				if (trees[i].containedRobot != null) {
    					Globals.setUnitRich(true);
    					break;
    				}
    			}
    			boolean allstuck = true;
    			for (int i = 0; i < enemyArchons.length; i++) {
    				if ((Globals.getArchonBits() & (int) Math.pow(2, i)) == 0) {
    					allstuck = false;
    					break;
    				}
    			}
    			
    			for (int i = 0; i < enemyArchons.length; i++) {
    				// if the enemy archon isnt stuck, create an order
    				if ((Globals.getArchonBits() & (int) Math.pow(2, i)) == 0 || allstuck) {
    					Memory.addOrder(new Order(0, enemyArchons[i], rc.getRoundLimit(), -1));
    				}
    			}
    		}
    	
    		BuildManager.checkDonateVP();
    		if (Globals.getStrat() == BuildManager.UNDECIDED && rc.getRoundNum() >= 2) {
    			BuildManager.decideBuild();
    		}
    		
    		Debug.debug_bytecode_start();
    		Memory.pruneOrders();
    		Debug.debug_bytecode_end("orders");
    		
    		isFirst = true;
    	} else {
    		isFirst = false;
    	}

    	Globals.updateUnitCounts();

    	if (rc.getType() != RobotType.GARDENER)
    	{
    		// NOTE we only set this for units that need it
    		enemiesMaxRange = rc.senseNearbyRobots(-1, enemy);
    		
    		OrderManager.updateOrders();
    		
        	if (OrderManager.shouldMove()){
        		rc.setIndicatorLine(rc.getLocation(), OrderManager.getTarget(), 255, 255, 255);
        	}
    	}
 
    }
    
    protected static void endTurn() throws GameActionException {
        Utilities.tryShake();
		try {
			Globals.updateEdges();
		} catch (Exception e) {
			Debug.debug_print("Error while updating edges / memory");
		}
    	Clock.yield();
    }
    
    public static Direction neo() throws GameActionException
    {
    	Debug.debug_bytecode_start();
    	
    	if (rc.getRoundNum() - goalDate >= 25)
    	{
    		goalDate = rc.getRoundNum();
    		goal = new Direction((float) rand.nextDouble() - 0.5f,(float) rand.nextDouble() - 0.5f);
    	}
    	
    	double xPressure = (float) (rand.nextDouble() - 0.5) * 20;
    	double yPressure = (float) (rand.nextDouble() - 0.5) * 20;
    	
    	double relativeX = 0.0;
    	double relativeY = 0.0;
   
    	Debug.debug_print("AX: " + xPressure);
    	Debug.debug_print("AY: " + yPressure);
    	
    	
    	//if not gardener or archon stay near enemy bots
    	if (rc.getType() != RobotType.ARCHON && rc.getType() != RobotType.GARDENER)
    	{
    		
        	for (int botCount = 0; botCount < enemiesMaxRange.length; botCount++)
        	{
        		relativeX = enemiesMaxRange[botCount].getLocation().x - rc.getLocation().x;
        		relativeY = enemiesMaxRange[botCount].getLocation().y - rc.getLocation().y;
        			
        		if (enemiesMaxRange[botCount].getType() == RobotType.GARDENER)
        		{
        			//be very attracted to enemy gardeners
        			xPressure += relativeX * 30 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
            		yPressure += relativeY * 30 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
            		if (rc.getType() == RobotType.SCOUT) // especially if a scout
            		{
            			xPressure += relativeX * 45 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location)  / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
                		yPressure += relativeY * 45 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location)  / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
            		}
        		}	
        		else if (enemiesMaxRange[botCount].getType() == RobotType.ARCHON && rc.getRoundNum() > 250)
        		{
        			if (rc.getRoundNum() > 500 || !OrderManager.isStuckArchon(enemiesMaxRange[botCount].location))
        			{
        				//be very attracted to enemy archons
        				xPressure += relativeX * 40 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
        				yPressure += relativeY * 40 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
        			}
        		}	
        		else
        		{
        			xPressure += relativeX / 1 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
            		yPressure += relativeY / 1 / rc.getLocation().distanceTo(enemiesMaxRange[botCount].location);
        		}
        		
        	}
    	}
    	Debug.debug_print("BX: " + xPressure);
    	Debug.debug_print("BY: " + yPressure);
    	
    	
    	//don't get too close to other bots
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(5);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].getType() != RobotType.LUMBERJACK || avoidBots[botCount].getTeam() != rc.getTeam())
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;

    			if (rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.LUMBERJACK) // archons and lumberjacks should avoid allies more
    			{
    				xPressure += -100 * (relativeX + Math.copySign(1, relativeX)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
        			yPressure += -100 * (relativeY + Math.copySign(1, relativeY)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
    			}
    			else
    			{
    				if ((avoidBots[botCount].getType() != RobotType.GARDENER && avoidBots[botCount].getType() != RobotType.ARCHON) || avoidBots[botCount].getTeam() == rc.getTeam())
    				{
    					xPressure += -50 * (relativeX + Math.copySign(1,  relativeX))/ rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
            			yPressure += -50 * (relativeY + Math.copySign(1, relativeY)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
    				}
    				
    			}
    			//archons and scouts should avoid enemies more except for archons and gardeners
    			if (avoidBots[botCount].getTeam() != rc.getTeam() && avoidBots[botCount].getType() != RobotType.ARCHON && avoidBots[botCount].getType() != RobotType.GARDENER && (rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.SCOUT))
    			{
    				xPressure += -250 * (relativeX + Math.copySign(1,  relativeX)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
        			yPressure += -250 * (relativeY + Math.copySign(1, relativeY)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
    			}
	
    		}
    	}
    	Debug.debug_print("CX: " + xPressure);
    	Debug.debug_print("CY: " + yPressure);
    	
    	//avoid friendly gardeners a lot if not soldier all inning
    	if (Globals.getStrat() != BuildManager.AGGRESSIVE)
    	{
    		avoidBots = rc.senseNearbyRobots(2 + rc.getType().bodyRadius);
        	for (int botCount = 0; botCount < avoidBots.length; botCount++)
        	{
        		relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
        			
        		if (avoidBots[botCount].type == RobotType.GARDENER && avoidBots[botCount].team == rc.getTeam())
        		{
        			xPressure += -700 * (relativeX + Math.copySign(10,  relativeX)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
        			yPressure += -700 * (relativeY + Math.copySign(10, relativeY)) / rc.getLocation().distanceTo(avoidBots[botCount].location) / rc.getLocation().distanceTo(avoidBots[botCount].location);
        		}
        	}
        	Debug.debug_print("DX: " + xPressure);
        	Debug.debug_print("DY: " + yPressure);
    	}
    	
    	
    	//if gardener or archon, avoid trees
    	if (rc.getType() == RobotType.GARDENER || rc.getType() == RobotType.ARCHON)
    	{
    		Debug.debug_bytecode_start();
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(7);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        		
        		if ((rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.GARDENER) && avoidTrees[treeCount].team == rc.getTeam())
        		{
        			xPressure += -120 * avoidTrees[treeCount].radius / (relativeX + Math.copySign(1,  relativeX));
            		yPressure += -120 * avoidTrees[treeCount].radius / (relativeY + Math.copySign(1, relativeY));
        		}
        		else
        		{
        			if (avoidTrees[treeCount].containedBullets > 0 && rc.getType() == RobotType.ARCHON)
        			{
        				xPressure += avoidTrees[treeCount].containedBullets * 5 * avoidTrees[treeCount].radius / (relativeX + Math.copySign(1,  relativeX));
                		yPressure += avoidTrees[treeCount].containedBullets * 5 * avoidTrees[treeCount].radius / (relativeY + Math.copySign(1, relativeY));
        			}
        			else
        			{
        				xPressure += -40 * avoidTrees[treeCount].radius / (relativeX + Math.copySign(1,  relativeX));
                		yPressure += -40 * avoidTrees[treeCount].radius / (relativeY + Math.copySign(1, relativeY));
        			}
        			
        		}
        		
        	} 
        	Debug.debug_bytecode_end("Tree pathing");
    	}
    	
    	//if gardener, move toward nearest edge
    	if (rc.getType() == RobotType.GARDENER)
    	{
    		
    		double distFromLowBound = 1000;
    		if (Globals.getBottomEdge() != -1)
    			{distFromLowBound = rc.getLocation().y - Globals.getBottomEdge();}
    		double distFromHighBound = 1000;
    		if (Globals.getTopEdge() != -1)
    			{distFromHighBound = Globals.getTopEdge() - rc.getLocation().y;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			if (distFromHighBound < 10)
    				{yPressure += -30;}
    			else if (distFromHighBound > 11)
    				{yPressure += 10;}	
    			Debug.debug_print("D1Y: " + yPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			if (distFromLowBound < 10)
    				{yPressure += 30;}
    			else if (distFromLowBound > 11)
    				{yPressure += -10;}	
    			Debug.debug_print("D2Y: " + yPressure);
    		}
    		
    		distFromLowBound = 1000;
    		if (Globals.getLeftEdge() != -1)
				{distFromLowBound = rc.getLocation().x - Globals.getLeftEdge();}
    		distFromHighBound = 1000;
    		if (Globals.getRightEdge() != -1)
				{distFromHighBound = Globals.getRightEdge() - rc.getLocation().x;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			Debug.debug_print("right edge at " + Globals.getRightEdge());
    			if (distFromHighBound < 10)
    				{xPressure += -30;}
    			else if (distFromHighBound > 11)
    				{xPressure += 10;}	
    			Debug.debug_print("D1X: " + xPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			Debug.debug_print("left edge at " + Globals.getLeftEdge());
    			Debug.debug_print("D2startX: " + xPressure);
    			if (distFromLowBound < 10)
    				{xPressure += 30;}
    			else if (distFromLowBound > 11)
    				{xPressure += -10;}	
    			Debug.debug_print("D2X: " + xPressure);
    		}
    	}
    	
    	//if archon or lumberjack, avoid edges
    	if (rc.getType() == RobotType.ARCHON)
    	{
    		if (Globals.getTopEdge() != -1)
    		{
    			relativeY = Globals.getTopEdge() - rc.getLocation().y;
    			yPressure += -3000/(relativeY * relativeY);
    			Debug.debug_print("D1Y: " + yPressure);
    		}
    		if (Globals.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Globals.getBottomEdge();
    			yPressure += 3000/(relativeY * relativeY);
    			Debug.debug_print("D2Y: " + yPressure);
    		}
    		
    		if (Globals.getRightEdge() != -1)
    		{
    			relativeX = Globals.getRightEdge() - rc.getLocation().x;
    			xPressure += -3000/(relativeX * relativeX);	
    			Debug.debug_print("D1X: " + xPressure);
    		}
    		if (Globals.getLeftEdge() != -1)
    		{
    			relativeX = rc.getLocation().x - Globals.getLeftEdge();
    			xPressure += 3000/(relativeX * relativeX);
    			Debug.debug_print("D2X: " + xPressure);
    		}
    	}
    	
    	// move towards a target if we have one 
    	if (OrderManager.shouldMove()){
    		
    		Debug.debug_print("Moving towards target");
        	relativeX = OrderManager.getTarget().x - rc.getLocation().x;
    		relativeY = OrderManager.getTarget().y - rc.getLocation().y;
    			
    		if (rc.getType() == RobotType.ARCHON) {
        		xPressure -= 40 * relativeX/rc.getLocation().distanceTo(OrderManager.getTarget());
        		yPressure -= 40 * relativeY/rc.getLocation().distanceTo(OrderManager.getTarget());    			
    		} else {
        		xPressure += 40 * relativeX/rc.getLocation().distanceTo(OrderManager.getTarget());
        		yPressure += 40 * relativeY/rc.getLocation().distanceTo(OrderManager.getTarget());
    		}

    	}
    	
    	Debug.debug_print("EX: " + xPressure);
    	Debug.debug_print("EY: " + yPressure);
    	
    	//if scout, gardener, or archon, follow goal according to goal age
    	if (rc.getType() == RobotType.SCOUT || rc.getType() == RobotType.GARDENER || rc.getType() == RobotType.ARCHON)
    	{
    		xPressure += Math.cos(goal.radians) * 2 * (rc.getRoundNum() - goalDate);
    		yPressure += Math.sin(goal.radians) * 2 * (rc.getRoundNum() - goalDate);
    		Debug.debug_print("GoalX: " + xPressure);
        	Debug.debug_print("GoalY: " + yPressure);
    	}
    	
    	TreeInfo[] bugNeutralTrees = rc.senseNearbyTrees(rc.getLocation().add(new Direction ((float) xPressure, (float) yPressure), rc.getType().bodyRadius), 1f, Team.NEUTRAL);
    	TreeInfo[] bugAllyTrees = rc.senseNearbyTrees(rc.getLocation().add(new Direction ((float) xPressure, (float) yPressure), rc.getType().bodyRadius), 1f, rc.getTeam());
    	//avoid previous location - AKA pressure bug
    	if (bugNeutralTrees.length > 0 || bugAllyTrees.length > 0)
    	{
    		rc.setIndicatorDot(lastPosition, 50, 150, 250);
    		if (lastPosition.equals(rc.getLocation()) == false)
    		{
    			
    			rc.setIndicatorLine(lastPosition, lastPosition.add(lastPosition.directionTo(rc.getLocation()).rotateLeftRads((float) (Math.PI / 2 - Math.PI / 36)), 3), 0,0,0);
    			rc.setIndicatorLine(lastPosition, lastPosition.add(lastPosition.directionTo(rc.getLocation()).rotateRightRads((float) (Math.PI / 2 - Math.PI / 36)), 3), 0,0,0);
    			
    			float rotateX = 0;
    			float rotateY = 0;
    			
    			double perp = 0;
    			double straight = 0;
	        			
    			relativeX = lastPosition.x - rc.getLocation().x;
    			relativeY = lastPosition.y - rc.getLocation().y;
        	
    			rotateX = (float) (relativeX * Math.cos(Math.PI / 36) - relativeY * Math.sin(Math.PI / 36));
    			rotateY = (float) (relativeX * Math.sin(Math.PI / 36) + relativeY * Math.cos(Math.PI / 36));
        	
    			Debug.debug_print("Rotate left x: " + rotateX);
    			Debug.debug_print("Rotate left y: " + rotateY);
        		
	        	perp = (yPressure - xPressure * rotateY / (rotateX + Math.copySign(0.01, rotateX)))/ ((rotateX + (rotateY * rotateY) + Math.copySign(0.01, rotateX + (rotateY * rotateY)))/(rotateX + Math.copySign(0.01, rotateX)));
	        	straight = Math.min(0,  xPressure / (rotateX + Math.copySign(0.01, rotateX)) + rotateY * perp/(rotateX + Math.copySign(0.01, rotateX)));
	        		
	        	xPressure = straight * relativeX - perp * relativeY;
	        	yPressure = perp * relativeX + straight * relativeY;
	        	
	        	rotateX = (float) (relativeX * Math.cos(Math.PI / -36) - relativeY * Math.sin(Math.PI / -36));
	        	rotateY = (float) (relativeX * Math.sin(Math.PI / -36) + relativeY * Math.cos(Math.PI / -36));
	        	
	        	Debug.debug_print("Rotate right x: " + rotateX);
	        	Debug.debug_print("Rotate right y: " + rotateY);
	        		
	        	perp = (yPressure - xPressure * rotateY / (rotateX + Math.copySign(0.01, rotateX)))/ ((rotateX + (rotateY * rotateY) + Math.copySign(0.01, rotateX + (rotateY * rotateY)))/(rotateX + Math.copySign(0.01, rotateX)));
	        	straight = Math.min(0,  xPressure / (rotateX + Math.copySign(0.01, rotateX)) + rotateY * perp/(rotateX + Math.copySign(0.01, rotateX)));
	        	
	        	xPressure = straight * relativeX - perp * relativeY;
	        	yPressure = perp * relativeX + straight * relativeY;
	        	
	        	Debug.debug_print("BugX: " + xPressure);
	        	Debug.debug_print("BugY: " + yPressure);
	        	
	        	xPressure += -30 * relativeX / rc.getLocation().distanceTo(lastPosition);
	        	yPressure += -30 * relativeY / rc.getLocation().distanceTo(lastPosition);
	        	
	        	Debug.debug_print("Relative X: " + relativeX);
	        	Debug.debug_print("Relative Y: " + relativeY);
	        	
	        	Debug.debug_print("repelX: " + xPressure);
	        	Debug.debug_print("repelY: " + yPressure);
    		}
    	}
    	
    	Debug.debug_bytecode_end("neo");
	
    	return new Direction((float)xPressure, (float) yPressure);  
    }
}