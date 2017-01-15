package testBot;

import battlecode.common.*;

public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
    protected static int memory_loc = -1;
	public static MapLocation archonStart;
	public static int behaviorType = 0;
	
    protected static void Init(RobotController RobCon){
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	archonStart = rc.getInitialArchonLocations(ally)[0];
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
    	double xPressure = (float) (Math.random() - 0.5) * 40;
    	double yPressure = (float) (Math.random() - 0.5) * 40;
    	
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
    		
    		if (pathDistance > -2)
    		{
    			double timeToDodge = pathDistance/bullets[bulletCount].getSpeed();
    			xPressure +=(bullets[bulletCount].damage * -1000 / bulletXVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    			yPressure +=(bullets[bulletCount].damage * 1000/bulletYVel / (pathOffset + Math.copySign(10,  pathOffset)) / (timeToDodge+10));
    		}
    	}
    	
    	//stay near other bots
    	if (rc.getType() != RobotType.ARCHON && rc.getType() != RobotType.GARDENER)
    	{
    		RobotInfo[] engageBots = rc.senseNearbyRobots(7);
        	for (int botCount = 0; botCount < engageBots.length; botCount++)
        	{
        		if (engageBots[botCount].team != rc.getTeam())
        		{
        			relativeX = engageBots[botCount].getLocation().x - rc.getLocation().x;
        			relativeY = engageBots[botCount].getLocation().y - rc.getLocation().y;
        		
        			xPressure += relativeX / 40;
        			yPressure += relativeY / 40;
        		}
        	}
    	}
    
    	
    	
    	//don't get too close to other bots
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(5);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].getType() != RobotType.LUMBERJACK && (avoidBots[botCount].team != rc.getTeam() || true))
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    			
    			xPressure += -50 / relativeX;
    			yPressure += -50 / relativeY;
    		}
    	}
    	
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
    				xPressure += -500 / relativeX;
    				yPressure += -500 / relativeY;
    			}
    		}
    	}
    	
    	//if gardener, avoid trees
    	if (rc.getType() == RobotType.GARDENER)
    	{
    		TreeInfo[] avoidTrees = rc.senseNearbyTrees(5);
        	for (int treeCount = 0; treeCount < avoidTrees.length; treeCount++)
        	{
        		relativeX = avoidTrees[treeCount].getLocation().x - rc.getLocation().x;
        		relativeY = avoidTrees[treeCount].getLocation().y - rc.getLocation().y;
        			
        		xPressure += -50 / relativeX;
        		yPressure += -50 / relativeY;
        	} 
    	}
    	
    	
    	//if gardener, move toward nearest edge
    	if (rc.getType() == RobotType.GARDENER && behaviorType == 0)
    	{
    		if (Memory.getTopEdge() != -1)
    		{
    			relativeY = Memory.getTopEdge() - rc.getLocation().y;
    			if (relativeY < 2)
    				{yPressure += -10;}
    			else if (relativeY > 5)
    				{yPressure += 30;}	
    		}
    		if (Memory.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getTopEdge();
    			if (relativeY < 2)
    				{yPressure += 10;}
    			else if (relativeY > 5)
    				{yPressure += -30;}	
    		}
    		
    		if (Memory.getRightEdge() != -1)
    		{
    			relativeY = Memory.getRightEdge() - rc.getLocation().x;
    			if (relativeX < 2)
    				{xPressure += -10;}
    			else if (relativeX > 5)
    				{xPressure += 30;}	
    		}
    		if (Memory.getLeftEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getLeftEdge();
    			if (relativeX < 2)
    				{xPressure += 10;}
    			else if (relativeX > 5)
    				{xPressure += -30;}	
    		}
    	}
    	
    	//if archon, avoid edges
    	if (rc.getType() == RobotType.ARCHON)
    	{
    		if (Memory.getTopEdge() != -1)
    		{
    			relativeY = Memory.getTopEdge() - rc.getLocation().y;
    			yPressure += -100/relativeY;
    		}
    		if (Memory.getBottomEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getTopEdge();
    			yPressure += 100/relativeY;
    		}
    		
    		if (Memory.getRightEdge() != -1)
    		{
    			relativeX = Memory.getRightEdge() - rc.getLocation().x;
    			xPressure += 100/relativeX;	
    		}
    		if (Memory.getLeftEdge() != -1)
    		{
    			relativeY = rc.getLocation().y - Memory.getLeftEdge();
    			xPressure += -100/relativeX;	
    		}
    	}
    	
    	return new Direction((float)xPressure, (float) yPressure);  
    }
}