package first_bot;

import java.util.HashMap;
import java.util.HashSet;

import battlecode.common.*;

public class Bot {
	public static RobotController rc;
    protected static Team ally;
    protected static Team enemy;
	public static HashMap<Integer, Integer> Mirror = new HashMap<Integer, Integer>();
	public static HashSet<Integer> Updated = new HashSet<Integer>();
    protected static void Init(RobotController RobCon){
    	rc = RobCon;
    	ally = rc.getTeam();
    	enemy = ally.opponent();
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