package pathBot;

import battlecode.common.*;

public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
    protected static int memory_loc = -1;
	public static MapLocation archonStart, enemyStarts[], enemyPing;
	public static int behaviorType = 0;
	
    protected static void Init(RobotController RobCon){
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	archonStart = rc.getInitialArchonLocations(ally)[0];
    	enemyStarts = rc.getInitialArchonLocations(enemy);
    	enemyPing = enemyStarts[(int) (Math.random() * enemyStarts.length)];
    	
    	// this should put a new bot into memory
    	try {
			Memory.write(Memory.getNumAllies() + 20, new AllyData(Utilities.typeToNumber(rc.getType()), rc.getLocation(), (int) rc.getHealth(), rc.getRoundNum() % 2 == 0).toInt());
			memory_loc = Memory.getNumAllies() + 20;
			Memory.setNumAllies(Memory.getNumAllies() + 1);
		} catch (Exception e) {
			System.out.println("Weird error while updating our location in memory");
			e.printStackTrace();
		}
    	
    	try {
			Memory.updateEdges();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    protected static void startTurn() throws Exception 
    {
    	if (rc.getTeamBullets() > 1000)
    	{
    		rc.donate(1000);
    	}
    	if (rc.getRoundLimit() - rc.getRoundNum() < 100)
    	{
    		rc.donate(rc.getTeamBullets() - rc.getTeamBullets() % 10);
    	}
    	// do binary search to find our location in memory
    	if (true) {
    		return;
    	}
    	
    	memory_loc = -1;
    	int my_loc = Utilities.targetToInt(rc.getLocation());
    	
    	int low = 20;
    	int high = Memory.getNumAllies() + 20;
    	while (low <= high) {
    		int mid = Math.round((low + high) / 2);
    		int their_loc = Utilities.bitInterval(Memory.read(mid), 11, 28);
    		if (their_loc == my_loc) {
    			memory_loc = mid;
    			break;
    		} else if (their_loc > my_loc) {
    			high = mid - 1;
    		} else if (their_loc < my_loc) {
    			low = mid + 1;
    		}
    	}

    	if (memory_loc == -1) {
    		throw new Exception("We couldn't locate ourselves in memory");
    	}
    }
    
    protected static void endTurn() throws GameActionException{
    	AllyData me = new AllyData(Memory.read(memory_loc));
    	me.location = rc.getLocation();
    	me.alive = rc.getRoundNum() % 2 == 0;
    	// TODO uncomment this when we fix memory
    	//rc.broadcast(memory_loc, me.toInt());
    	Clock.yield();
    }
    
    public static Direction neo() throws GameActionException
    {
    	BulletInfo[] bullets = rc.senseNearbyBullets();
    	double xPressure = (float) (Math.random() - 0.5) * 20;
    	double yPressure = (float) (Math.random() - 0.5) * 20;
    	
    	double pathDistance = 0.0f;
    	double pathOffset = 0.0f;
    	
    	double bulletXVel = 0.0;
    	double bulletYVel = 0.0;
    	
    	double relativeX = 0.0;
    	double relativeY = 0.0;
    	
    	//dodge bullets
    	
    	for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
    	{
    		bulletXVel = bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].getDir().radians);
    		bulletYVel = bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].getDir().radians);
    		
    		relativeX = bullets[bulletCount].getLocation().x - rc.getLocation().x;
    		relativeY = bullets[bulletCount].getLocation().y - rc.getLocation().y;
    		
    		pathOffset = (relativeY - bulletYVel * relativeX/bulletXVel)/(-1/bulletYVel - bulletYVel);
    		pathDistance = relativeX/bulletXVel - pathOffset;
    		
    		if (pathDistance > -0.2)
    		{
    			double timeToDodge = pathDistance/bullets[bulletCount].getSpeed();
    			xPressure +=(bullets[bulletCount].damage * -500 / bulletXVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    			yPressure +=(bullets[bulletCount].damage * 500/bulletYVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    		}
    	}
    	System.out.println("AX: " + xPressure);
    	System.out.println("AY: " + yPressure);
    	
    	//if not gardener or archon stay near enemy bots
    	if (rc.getType() != RobotType.ARCHON && rc.getType() != RobotType.GARDENER)
    	{
    		RobotInfo[] engageBots = rc.senseNearbyRobots(7);
        	for (int botCount = 0; botCount < engageBots.length; botCount++)
        	{
        		if (engageBots[botCount].team != rc.getTeam())
        		{
        			relativeX = engageBots[botCount].getLocation().x - rc.getLocation().x;
        			relativeY = engageBots[botCount].getLocation().y - rc.getLocation().y;
        			
        			if (engageBots[botCount].getType() == RobotType.GARDENER)
        			{
        				//be very attracted to enemy gardeners
        				xPressure += relativeX * 30;
            			yPressure += relativeY * 30;
        			}	
        			else if (engageBots[botCount].getType() == RobotType.ARCHON)
        			{
        				//be very attracted to enemy archons
        				xPressure += relativeX * 10;
            			yPressure += relativeY * 10;
        			}	
        			else
        			{
        				xPressure += relativeX / 40;
            			yPressure += relativeY / 40;
        			}
        		
        		}
        	}
    	}
    	System.out.println("BX: " + xPressure);
    	System.out.println("BY: " + yPressure);
    	
    	
    	//don't get too close to other bots
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(5);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].getType() != RobotType.LUMBERJACK && (avoidBots[botCount].team != rc.getTeam() || true))
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    			xPressure += -50 / (relativeX + Math.copySign(1,  relativeX));
    			yPressure += -50 / (relativeY + Math.copySign(1, relativeY));
    		}
    	}
    	System.out.println("CX: " + xPressure);
    	System.out.println("CY: " + yPressure);
    	
    	//avoid friendly gardeners a lot
    	
    	avoidBots = rc.senseNearbyRobots(2);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].team != rc.getTeam() || true)
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    			if (avoidBots[botCount].type == RobotType.GARDENER && avoidBots[botCount].team == rc.getTeam())
    			{
    				xPressure += -500 / (relativeX + Math.copySign(1,  relativeX));
    				yPressure += -500 / (relativeY + Math.copySign(1, relativeY));
    			}
    		}
    	}
    	System.out.println("DX: " + xPressure);
    	System.out.println("DY: " + yPressure);
    	
    	//if gardener, avoid trees
    	if (rc.getType() == RobotType.GARDENER)
    	{
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(5);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        			
        		xPressure += -100 / (relativeX + Math.copySign(1,  relativeX));
        		yPressure += -100 / (relativeY + Math.copySign(1, relativeY));
        	} 
    	}
    	
    	
    	
    	//if gardener, move toward nearest edge
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0)
    	{
    		if (Memory.getTopEdge() != -1)
    		{
    			relativeY = Memory.getTopEdge() - rc.getLocation().y;
    			if (relativeY < 2)
    				{yPressure += -30;}
    			else if (relativeY > 5)
    				{yPressure += 60;}	
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (Memory.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getTopEdge();
    			if (relativeY < 2)
    				{yPressure += 30;}
    			else if (relativeY > 5)
    				{yPressure += -60;}	
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		if (Memory.getRightEdge() != -1)
    		{
    			relativeY = Memory.getRightEdge() - rc.getLocation().x;
    			if (relativeX < 2)
    				{xPressure += -30;}
    			else if (relativeX > 5)
    				{xPressure += -60;}	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (Memory.getLeftEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getLeftEdge();
    			if (relativeX < 2)
    				{xPressure += 30;}
    			else if (relativeX > 5)
    				{xPressure += 60;}	
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	//if archon, avoid edges
    	if (rc.getType() == RobotType.ARCHON)
    	{
    		if (Memory.getTopEdge() != -1)
    		{
    			relativeY = Memory.getTopEdge() - rc.getLocation().y;
    			yPressure += -200/relativeY;
    			System.out.println("D1Y: " + yPressure);
    		}
    		if (Memory.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getTopEdge();
    			yPressure += 200/relativeY;
    			System.out.println("D2Y: " + yPressure);
    		}
    		
    		if (Memory.getRightEdge() != -1)
    		{
    			relativeX = Memory.getRightEdge() - rc.getLocation().x;
    			xPressure += -200/relativeX;	
    			System.out.println("D1X: " + xPressure);
    		}
    		if (Memory.getLeftEdge() != -1)
    		{
    			relativeX = rc.getLocation().x - Memory.getLeftEdge();
    			xPressure += 200/relativeX;
    			System.out.println("D2X: " + xPressure);
    		}
    	}
    	
    	//aggress to enemy spawn if soldier
    	
    	if (rc.getType() == RobotType.SOLDIER)
    	{
    		relativeX = enemyPing.x - rc.getLocation().x;
			relativeY = enemyPing.y - rc.getLocation().y;
			
			xPressure += relativeX / 1;
			yPressure += relativeY / 1;
    	}
    	
    	System.out.println("EX: " + xPressure);
    	System.out.println("EY: " + yPressure);
    	
    	//path around trees
    	if (true)
    	{
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(2);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        		
        		double Q = (yPressure - (xPressure * relativeY / relativeX)) / (-1/relativeY - relativeY);
        		double R = Math.min(0,  xPressure/relativeX - Q);
        		//double R = 0;
        		
        		xPressure = relativeX * (R + Q);
        		yPressure = R * relativeY - Q/relativeY;
        	} 
    	}
    	System.out.println("FX: " + xPressure);
    	System.out.println("FY: " + yPressure);
    	
    	return new Direction((float)xPressure, (float) yPressure);  
    }
}