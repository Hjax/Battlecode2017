package first_bot;

import battlecode.common.*;

public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
	public static MapLocation archonStart;
	
    protected static void Init(RobotController RobCon){
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
    	archonStart = rc.getInitialArchonLocations(ally)[0];
    	// this should put a new bot into memory
    	try {
    		
			Memory.write(Memory.getNumAllies() + 20, new AllyData(Utilities.typeToNumber(rc.getType()), rc.getLocation(), (int) rc.getHealth(), rc.getRoundNum() % 2 == 1).toInt());
			Memory.setNumAllies(Memory.getNumAllies() + 1);
			System.out.print("Set number of allies to: ");
			System.out.println(Memory.getNumAllies());
			Memory.commit();
		} catch (Exception e) {
			System.out.println("Weird error while updating our location in memory");
		}
    }
    
    protected static void startTurn() throws GameActionException {
    	// reset the mirror and the updated arrays
    	Memory.Mirror.clear();
    	Memory.Updated.clear();
    }
    
    protected static void endTurn() throws GameActionException{
    	Memory.commit();
    	Clock.yield();
    }
    
    public static Direction neo()
    {
    	BulletInfo[] bullets = rc.senseNearbyBullets();
    	double xPressure = 0.0f;
    	double yPressure = 0.0f;
    	
    	double pathDistance = 0.0f;
    	double pathOffset = 0.0f;
    	
    	double bulletXVel = 0.0;
    	double bulletYVel = 0.0;
    	
    	double relativeX = 0.0;
    	double relativeY = 0.0;
    	
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
    	
    	System.out.println("dodged bullets");
    	System.out.println(rc.getLocation().x);
    	
    	RobotInfo[] engageBots = rc.senseNearbyRobots(7);
    	for (int botCount = 0; botCount < engageBots.length; botCount++)
    	{
    		if (engageBots[botCount].team != rc.getTeam())
    		{
    			relativeX = engageBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = engageBots[botCount].getLocation().y - rc.getLocation().y;
    		
    			xPressure += relativeX / -100;
    			yPressure += relativeY / -100;
    		}
    	}
    	
    	System.out.println("engaged");
    	
    	RobotInfo[] avoidBots = rc.senseNearbyRobots(4);
    	for (int botCount = 0; botCount < avoidBots.length; botCount++)
    	{
    		if (avoidBots[botCount].team != rc.getTeam())
    		{
    			relativeX = avoidBots[botCount].getLocation().x - rc.getLocation().x;
    			relativeY = avoidBots[botCount].getLocation().y - rc.getLocation().y;
    		
    			xPressure += relativeX / 40;
    			yPressure += relativeY / 40;
    		}
    	}
    	
    	System.out.println("pathed");
    	if (xPressure == 0 && yPressure == 0)
    		return new Direction((float)Math.random() * 2 * (float)Math.PI);
    	else return new Direction((float)xPressure, (float) yPressure);
    	
    
    }
}