package seedingWithPressureReset;

import java.util.Random;

import battlecode.common.*;


public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
    protected static int memory_loc = -1;
	public static MapLocation[] allyArchons;
	public static MapLocation[] enemyArchons;
	public static int behaviorType = 0;
	public static boolean isFirst = false;
	public static Random rand;
	public static MapLocation lastPosition;
	public static RobotInfo[] enemiesMaxRange = new RobotInfo[0];
	
    protected static void Init(RobotController RobCon) throws GameActionException{
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	allyArchons = rc.getInitialArchonLocations(ally);
    	enemyArchons = rc.getInitialArchonLocations(enemy);
    	lastPosition = rc.getLocation();
    	
    	rand = new Random(rc.getID());
    	
    	if (rc.getType() == RobotType.GARDENER && Globals.getTrainerCount() < Math.floor((Globals.getGardenerCount())/3))
    	{
    		System.out.println(Globals.getTrainerCount());
    		System.out.println(Globals.getGardenerCount());
    		behaviorType = 1;
    	}
    	
    	// this should put a new bot into memory
    	try {
    		memory_loc = Memory.first_free_ally();
    		System.out.print("I got assigned memory loc");
    		System.out.println(memory_loc);
    		Memory.reserveAllyIndex(memory_loc);
    		AllyData me = new AllyData(UnitType.getType(), rc.getLocation(), (int) rc.getHealth(), rc.getRoundNum() % 2 == 0);
			Memory.writeAllyData(memory_loc, me);
			Globals.incrementUnitCount(me.type, 1);
		} catch (Exception e) {
			System.out.println("Weird error while updating our location in memory");
			e.printStackTrace();
		}
    	
    	try {
			Globals.updateEdges();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    protected static void startTurn() throws Exception 
    {
    	
    	System.out.println("I live at: " + Integer.toString(memory_loc + Memory.min_ally));
    	if (rc.getType() != RobotType.GARDENER || behaviorType == 1)
    	{
    		// NOTE we only set this for units that need it
    		enemiesMaxRange = rc.senseNearbyRobots(-1, enemy);
    		OrderManager.updateOrders();
        	if (OrderManager.shouldMove()){
        		rc.setIndicatorLine(rc.getLocation(), OrderManager.getTarget(), 255, 255, 255);
        	}
    	}
    	
    	Memory.updateMyMemory();
    	
    	if (rc.getTeamBullets() > 1000 || rc.getRoundLimit() - rc.getRoundNum() < 100){
    		rc.donate((float) (rc.getTeamBullets() - rc.getTeamBullets() % (7.5 + (rc.getRoundNum() * 12.5 / 3000))));
    	}
    	if (Globals.getRoundNumber() != rc.getRoundNum()){
    		if (rc.getRoundNum() == 1) {
    			// we went first on the first round, add enemy archon spawns as orders
    			for (int i = 0; i < enemyArchons.length; i++){
    				Memory.addOrder(new Order(0, enemyArchons[i], rc.getRoundLimit(), -1));
    			}
    			Memory.clearAllies();
    		}
    		
    		Globals.setRoundNumber(rc.getRoundNum());
    		int start = Clock.getBytecodeNum();
    		Memory.pruneAllyMemory();
    		System.out.print("Defrag took: ");
    		System.out.println(Clock.getBytecodeNum() - start);
    		start = Clock.getBytecodeNum();
    		Memory.pruneOrders();
    		System.out.print("Orders took: ");
    		System.out.println(Clock.getBytecodeNum() - start);
    		isFirst = true;
    	} else {
    		isFirst = false;
    	}
    }
    
    protected static void endTurn() throws GameActionException {
    	Memory.updateMyMemory();
		try {
			Globals.updateEdges();
		} catch (Exception e) {
			System.out.println("Error while updating edges");
		}
        Utilities.tryShake();
    	Clock.yield();
    }
    
    public static Direction neo() throws GameActionException
    {
    	int bytecodeUsed = Clock.getBytecodeNum();
    	
    	double xPressure = (float) (rand.nextDouble() - 0.5) * 20;
    	double yPressure = (float) (rand.nextDouble() - 0.5) * 20;
    	
    	double relativeX = 0.0;
    	double relativeY = 0.0;
    	
    	//dodge bullets
    	if (rc.getType() != RobotType.TANK)
    	{
    		BulletInfo[] bullets = rc.senseNearbyBullets();
        	
        	double pathDistance = 0.0f;
        	double pathOffset = 0.0f;
        	
        	double bulletXVel = 0.0;
        	double bulletYVel = 0.0;
        	
        	
        	
    		for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
        	{
        		bulletXVel = bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].getDir().radians);
        		bulletYVel = bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].getDir().radians);
        		
        		relativeX = rc.getLocation().x - bullets[bulletCount].getLocation().x;
        		relativeY = rc.getLocation().y - bullets[bulletCount].getLocation().y;
        		
        		pathOffset = (relativeY - relativeX * bulletYVel / bulletXVel) / (bulletXVel + (bulletYVel * bulletYVel)/bulletXVel);
        		pathDistance = relativeX/bulletXVel + bulletYVel * pathOffset / bulletXVel;
        		
        		if (pathDistance > -0.2 && pathDistance < 4)
        		{
        			double timeToDodge = pathDistance/bullets[bulletCount].getSpeed();
        			System.out.println("PathOffset: " + pathOffset);
        			System.out.println("bulletXVel: " + bulletXVel);
        			System.out.println("relativeX: " + relativeX);
        			xPressure += (bullets[bulletCount].damage * -500 * bulletYVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
        			yPressure += (bullets[bulletCount].damage * 500 * bulletXVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
        		}
        	}
        	System.out.println("AX: " + xPressure);
        	System.out.println("AY: " + yPressure);
    	}
    	
    	
    	
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
        			xPressure += relativeX * 30;
            		yPressure += relativeY * 30;
        		}	
        		else if (enemiesMaxRange[botCount].getType() == RobotType.ARCHON && rc.getRoundNum() < 250)
        		{
        			//be very attracted to enemy archons
        			xPressure += relativeX * 20;
            		yPressure += relativeY * 20;
        		}	
        		else
        		{
        			xPressure += relativeX / 1;
            		yPressure += relativeY / 1;
        		}
        		
        	}
    	}
    	System.out.println("BX: " + xPressure);
    	System.out.println("BY: " + yPressure);
    	
    	
    	//don't get too close to other bots
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(5);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].getType() != RobotType.LUMBERJACK)
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    			//gardeners should avoid archons and gardeners more
    			if (rc.getType() == RobotType.GARDENER && (avoidBots[botCount].getType() == RobotType.ARCHON || avoidBots[botCount].getType() == RobotType.GARDENER))
    			{
    				xPressure += -700 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -700 / (relativeY + Math.copySign(1, relativeY));
    			}
    			else if (rc.getType() == RobotType.ARCHON) // archons should avoid allies more
    			{
    				xPressure += -100 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -100 / (relativeY + Math.copySign(1, relativeY));
    			}
    			else
    			{
    				xPressure += -50 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -50 / (relativeY + Math.copySign(1, relativeY));
    			}
    			//archons should avoid enemies more
    			if (avoidBots[botCount].getTeam() != rc.getTeam() && rc.getType() == RobotType.ARCHON)
    			{
    				xPressure += -250 / (relativeX + Math.copySign(1,  relativeX));
        			yPressure += -250 / (relativeY + Math.copySign(1, relativeY));
    			}
	
    		}
    	}
    	System.out.println("CX: " + xPressure);
    	System.out.println("CY: " + yPressure);
    	
    	//avoid friendly gardeners a lot if not soldier all inning
    	if (Globals.getStrat() != 1)
    	{
    		avoidBots = rc.senseNearbyRobots(2 + rc.getType().bodyRadius);
        	for (int botCount = 0; botCount < avoidBots.length; botCount++)
        	{
        		relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
        			
        		if (avoidBots[botCount].type == RobotType.GARDENER && avoidBots[botCount].team == rc.getTeam())
        		{
        			xPressure += -1400 / (relativeX + Math.copySign(10,  relativeX));
        			yPressure += -1400 / (relativeY + Math.copySign(10, relativeY));
        		}
        	}
        	System.out.println("DX: " + xPressure);
        	System.out.println("DY: " + yPressure);
    	}
    	
    	
    	//if gardener or archon, avoid trees
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0 || rc.getType() == RobotType.ARCHON)
    	{
    		int treeCost = Clock.getBytecodeNum();
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(7);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        			
        		xPressure += -40 * avoidTrees[treeCount].radius / (relativeX + Math.copySign(1,  relativeX));
        		yPressure += -40 * avoidTrees[treeCount].radius / (relativeY + Math.copySign(1, relativeY));
        	} 
        	treeCost = Clock.getBytecodeNum() - treeCost;
        	System.out.println("tree pathing costs: " + treeCost);
    	}
    	
    	//if gardener, move toward nearest edge
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0)
    	{
    		
    		double distFromLowBound = 1000;
    		if (Globals.getBottomEdge() != -1)
    			{distFromLowBound = rc.getLocation().y - Globals.getBottomEdge();}
    		double distFromHighBound = 1000;
    		if (Globals.getTopEdge() != -1)
    			{distFromHighBound = Globals.getTopEdge() - rc.getLocation().y;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			if (distFromHighBound < 4)
    				{yPressure += -30;}
    			else if (distFromHighBound > 5)
    				{yPressure += 150;}	
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			if (distFromLowBound < 4)
    				{yPressure += 30;}
    			else if (distFromLowBound > 5)
    				{yPressure += -150;}	
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		distFromLowBound = 1000;
    		if (Globals.getLeftEdge() != -1)
				{distFromLowBound = rc.getLocation().x - Globals.getLeftEdge();}
    		distFromHighBound = 1000;
    		if (Globals.getRightEdge() != -1)
				{distFromHighBound = Globals.getRightEdge() - rc.getLocation().x;}
    		
    		if (distFromHighBound != 1000 && distFromHighBound < distFromLowBound)
    		{
    			System.out.println("right edge at " + Globals.getRightEdge());
    			if (distFromHighBound < 4)
    				{xPressure += -30;}
    			else if (distFromHighBound > 5)
    				{xPressure += 150;}	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (distFromLowBound != 1000 && distFromLowBound < distFromHighBound)
    		{
    			System.out.println("left edge at " + Globals.getLeftEdge());
    			System.out.println("D2startX: " + xPressure);
    			if (distFromLowBound < 4)
    				{xPressure += 30;}
    			else if (distFromLowBound > 5)
    				{xPressure += -150;}	
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	//if archon or lumberjack, avoid edges
    	if (rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.LUMBERJACK)
    	{
    		if (Globals.getTopEdge() != -1)
    		{
    			relativeY = Globals.getTopEdge() - rc.getLocation().y;
    			yPressure += -4000/(relativeY * relativeY);
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (Globals.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Globals.getBottomEdge();
    			yPressure += 4000/(relativeY * relativeY);
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		if (Globals.getRightEdge() != -1)
    		{
    			relativeX = Globals.getRightEdge() - rc.getLocation().x;
    			xPressure += -4000/(relativeX * relativeX);	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (Globals.getLeftEdge() != -1)
    		{
    			relativeX = rc.getLocation().x - Globals.getLeftEdge();
    			xPressure += 4000/(relativeX * relativeX);
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	// move towards a target if we have one 
    	if (OrderManager.hasOrder() && OrderManager.shouldMove()){
    			
        	relativeX = OrderManager.getTarget().x - rc.getLocation().x;
    		relativeY = OrderManager.getTarget().y - rc.getLocation().y;
    			
    		xPressure += 50 * relativeX/rc.getLocation().distanceTo(OrderManager.getTarget());
    		yPressure += 50 * relativeY/rc.getLocation().distanceTo(OrderManager.getTarget());
    	}
    	
    	System.out.println("EX: " + xPressure);
    	System.out.println("EY: " + yPressure);
    	
    	//avoid previous location - AKA pressure bug
    	if (rc.senseNearbyTrees(1 + rc.getType().bodyRadius, Team.NEUTRAL).length > 0 || rc.senseNearbyTrees(1 + rc.getType().bodyRadius, ally).length > 0)
    	{
    		rc.setIndicatorDot(lastPosition, 50, 150, 250);
    		if (lastPosition.equals(rc.getLocation()) == false)
    		{
    			rc.setIndicatorLine(lastPosition, lastPosition.add(lastPosition.directionTo(rc.getLocation()).rotateLeftDegrees(85), 3), 0,0,0);
    			rc.setIndicatorLine(lastPosition, lastPosition.add(lastPosition.directionTo(rc.getLocation()).rotateRightDegrees(85), 3), 0,0,0);
    			
    			relativeX = lastPosition.x - rc.getLocation().x;
    			relativeY = lastPosition.y - rc.getLocation().y;
        	
    			float rotateX = (float) (relativeX * Math.cos(Math.PI / 36) - relativeY * Math.sin(Math.PI / 36));
    			float rotateY = (float) (relativeX * Math.sin(Math.PI / 36) + relativeY * Math.cos(Math.PI / 36));
        	
    			System.out.println("Rotate left x: " + rotateX);
    			System.out.println("Rotate left y: " + rotateY);
        		
	        	double perp = (yPressure - xPressure * rotateY / (rotateX + Math.copySign(0.01, rotateX)))/ ((rotateX + (rotateY * rotateY) + Math.copySign(0.01, rotateX + (rotateY * rotateY)))/(rotateX + Math.copySign(0.01, rotateX)));
	        	double straight = Math.min(0,  xPressure / (rotateX + Math.copySign(0.01, rotateX)) + rotateY * perp/(rotateX + Math.copySign(0.01, rotateX)));
	        		
	        	xPressure = straight * relativeX - perp * relativeY;
	        	yPressure = perp * relativeX + straight * relativeY;
	        	
	        	rotateX = (float) (relativeX * Math.cos(Math.PI / -36) - relativeY * Math.sin(Math.PI / -36));
	        	rotateY = (float) (relativeX * Math.sin(Math.PI / -36) + relativeY * Math.cos(Math.PI / -36));
	        	
	        	System.out.println("Rotate right x: " + rotateX);
	        	System.out.println("Rotate right y: " + rotateY);
	        		
	        	perp = (yPressure - xPressure * rotateY / (rotateX + Math.copySign(0.01, rotateX)))/ ((rotateX + (rotateY * rotateY) + Math.copySign(0.01, rotateX + (rotateY * rotateY)))/(rotateX + Math.copySign(0.01, rotateX)));
	        	straight = Math.min(0,  xPressure / (rotateX + Math.copySign(0.01, rotateX)) + rotateY * perp/(rotateX + Math.copySign(0.01, rotateX)));
	        	
	        	xPressure = straight * relativeX - perp * relativeY;
	        	yPressure = perp * relativeX + straight * relativeY;
	        	
	        	System.out.println("BugX: " + xPressure);
	        	System.out.println("BugY: " + yPressure);
	        	
	        	xPressure += -30 * relativeX;
	        	yPressure += -30 * relativeY;
	        	
	        	System.out.println("Relative X: " + relativeX);
	        	System.out.println("Relative Y: " + relativeY);
	        	
	        	System.out.println("repelX: " + xPressure);
	        	System.out.println("repelY: " + yPressure);
    		}
    		
        		
        	
    	}
    	
    	bytecodeUsed = Clock.getBytecodeNum() - bytecodeUsed;
    	System.out.println("pathing takes: " + bytecodeUsed);
    			

    	
    	return new Direction((float)xPressure, (float) yPressure);  
    }
}