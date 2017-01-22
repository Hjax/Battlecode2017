package standardBot;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class Utilities extends Bot{

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException 
    {    	
        return tryMove(dir,10,10);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
    	int bytes = Clock.getBytecodeNum();
    	float distance = rc.getType().strideRadius;
        // First, try intended direction
        if (rc.canMove(dir) && rc.senseNearbyBullets(rc.getLocation().add(dir, distance), rc.getType().bodyRadius).length == 0) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;
        Direction tryLeft = dir;
        Direction tryRight = dir;
        
        while (distance >= 0.1f)
        {

        	while(currentCheck<=checksPerSide) 
        	{
        		// Try the offset of the left side
        		tryLeft = tryLeft.rotateLeftDegrees(degreeOffset);
        		if(rc.canMove(tryLeft) && rc.senseNearbyBullets(rc.getLocation().add(tryLeft, distance), rc.getType().bodyRadius).length == 0) {
        			rc.move(tryLeft);
        			bytes = Clock.getBytecodeNum() - bytes;
        			System.out.println("moving used " + bytes);
        			return true;
        		}
        		// Try the offset on the right side
        		tryRight = tryRight.rotateRightDegrees(degreeOffset);
        		if(rc.canMove(tryRight) && rc.senseNearbyBullets(rc.getLocation().add(tryRight, distance), rc.getType().bodyRadius).length == 0) {
        			rc.move(tryRight);
        			bytes = Clock.getBytecodeNum() - bytes;
        			System.out.println("moving used " + bytes);
        			return true;
        		}
        		// No move performed, try slightly further
        		currentCheck++;
        	}
        	distance -= 0.4f;
        	currentCheck = 1;
        }

        // A move never happened, so return false.
        bytes = Clock.getBytecodeNum() - bytes;
		System.out.println("moving used " + bytes);
		tryShake();
        return false;
    }

    static boolean willHitAlly(MapLocation target) {
    	
    	RobotInfo[] allies = rc.senseNearbyRobots(rc.getLocation().add(rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target) / 2), rc.getLocation().distanceTo(target) / 2, ally);
    	for (RobotInfo friend: allies) {
            // Calculate bullet relations to this robot
            Direction directionToRobot = rc.getLocation().directionTo(friend.location);
            float distToRobot = rc.getLocation().distanceTo(friend.location);
            float theta = rc.getLocation().directionTo(target).radiansBetween(directionToRobot);


            // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
            // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
            // This corresponds to the smallest radius circle centered at our location that would intersect with the
            // line that is the path of the bullet.
            float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

            if (perpendicularDist < friend.getType().bodyRadius) {
            	return true;
            }
    	}
    	TreeInfo[] alliedTrees = rc.senseNearbyTrees(rc.getLocation().distanceTo(target), ally);
    	for (TreeInfo friend: alliedTrees) {
            // Calculate bullet relations to this robot
            Direction directionToRobot = rc.getLocation().directionTo(friend.location);
            float distToRobot = rc.getLocation().distanceTo(friend.location);
            float theta = rc.getLocation().directionTo(target).radiansBetween(directionToRobot);


            // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
            // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
            // This corresponds to the smallest radius circle centered at our location that would intersect with the
            // line that is the path of the bullet.
            float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

            if (perpendicularDist < 1) {
            	return true;
            }
    	}
    	return false;
    }
    
	public static MapLocation intToTarget(int location){
		// so we have 32 bits in a java int 
		// the high 9 bits are the x, we can get them by dividing
		// the low  9 bits are the y, we can get them by mod
		//return new MapLocation(location / 1024, location % 1024); 
		int x = location / 512;
		int y = location % 512;
		x = ((x / 2) - 100) + Math.round(archonStart.x);
		y = ((y / 2) - 100) + Math.round(archonStart.y);
		return new MapLocation(x, y);
	}
	public static int targetToInt(MapLocation target) {
		int x = ((Math.round(target.x) - Math.round(archonStart.x)) + 100) * 2;
		int y = ((Math.round(target.y) - Math.round(archonStart.y)) + 100) * 2;
		return x * 512 + y;
	}
	
	
	// returns the closest edge in a given direction, -1 otherwise 
	public static float edgeInDirection(Direction dir) throws GameActionException {
		float max_dist = rc.getType().sensorRadius - 0.01f;
		System.out.println("map check1");
		System.out.println(dir.getAngleDegrees());
		if (rc.onTheMap(rc.getLocation().add(dir, max_dist))){
			return -1;
		}
		System.out.println("map check1 done");
		float resolution = rc.getType().sensorRadius / 2;
		max_dist -= resolution;
		while (resolution > 0.125) {
			resolution /= 2;
			System.out.println("map check2+");
			if (rc.onTheMap(rc.getLocation().add(dir, max_dist))){
				max_dist += resolution;
			} else {
				max_dist -= resolution;
			}
		}
		if (Math.round(dir.getAngleDegrees()) == Math.round(Direction.getNorth().getAngleDegrees()) ||
				Math.round(dir.getAngleDegrees()) == Math.round(Direction.getSouth().getAngleDegrees())) {
			System.out.println(max_dist);
			return rc.getLocation().add(dir, max_dist).y;
		}
		return rc.getLocation().add(dir, max_dist).x;
	}
	
	public static int bitInterval(long input, int start, int end) {
		// found this equation on stackoverflow
		return (int) ((input >> start) & ~(~0 << (end-start+1)));
	}
	
	public static void moveTo(MapLocation destination) throws GameActionException
	{
		if (rc.getLocation().equals(destination) == false)
		{
			float speed = rc.getType().strideRadius;
			float distance = rc.getLocation().distanceTo(destination);
			
			if (distance < speed && rc.canMove(destination))
			{
				rc.move(rc.getLocation().directionTo(destination), distance);
			}
			else tryMove(rc.getLocation().directionTo(destination));
		}
	}
	
	public static MapLocation melee(MapLocation targetPoint, float radius) throws GameActionException //radius should be the sum of the radii of the target and of self
	{
		Direction angle = rc.getLocation().directionTo(targetPoint);
		float distance = rc.getLocation().distanceTo(targetPoint);
		distance -= radius;
		rc.setIndicatorDot(rc.getLocation().add(angle, distance), 255, 0, 0);
		return rc.getLocation().add(angle, distance);
	}
	
	public static void tryShake() throws GameActionException
	{
		TreeInfo trees[] = rc.senseNearbyTrees(rc.getType().bodyRadius + 1, Team.NEUTRAL);
		for (int countTree = 0; countTree < trees.length; countTree++)
		{
			if (rc.canShake(trees[countTree].ID) && trees[countTree].containedBullets > 0)
			{
				rc.shake(trees[countTree].ID);
			}
		}
	}
	
	public static MapLocation dodgeBullet(BulletInfo bullet)
	{
		double bulletXVel = bullet.getSpeed() * Math.cos(bullet.getDir().radians);
		double bulletYVel = bullet.getSpeed() * Math.sin(bullet.getDir().radians);
		
		double relativeX = bullet.getLocation().x - rc.getLocation().x;
		double relativeY = bullet.getLocation().y - rc.getLocation().y;
		
		double pathOffset = relativeY - (bulletYVel * relativeX/bulletXVel)/(-1/bulletYVel - bulletYVel);
		double pathDistance = relativeX/bulletXVel - pathOffset;
		
		if (pathDistance - bullet.getSpeed() - rc.getType().bodyRadius <= 0 && pathOffset <= rc.getType().bodyRadius && pathDistance > 0)
		{
			return rc.getLocation().add(new Direction((float)bulletXVel, (float)bulletYVel).rotateRightDegrees((float)Math.copySign(90, pathOffset)), (float)pathOffset + 1.0f);
		}
		return rc.getLocation();
	}
	
	public static MapLocation magnitudePressureDodge(BulletInfo[] bullets)
	{
		return magnitudePressureDodge(bullets, null);
	}
	
	public static MapLocation magnitudePressureDodge(BulletInfo[] bullets, MapLocation destination)
	{
		float destX = 0;
		float destY = 0;
		if (destination != null)
		{
			destX = destination.x;
			destY = destination.y;
		}
		float x = rc.getLocation().x;
		float y = rc.getLocation().y;
		float pressureMultiplier = 0.7f;
		float xPres = 0f;
		float yPres = 0f;
		float bulletXVel = 0f;
		float bulletYVel = 0f;
		float relativeX = 0f;
		float relativeY = 0f;
		float pathOffset = 0f;
		float pathDistance = 0f;
		float bulletYOverX = 0f;
		float cubed = 0f;
		double angle = 0;
		while ((Clock.getBytecodesLeft() -2750) / bullets.length > 150)
		{
			for (int bulletCount = 0; bulletCount < bullets.length; bulletCount++)
			{
				bulletXVel = (float) (bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].dir.radians));
				bulletYVel = (float) (bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].dir.radians));
				bulletYOverX = bulletYVel / bulletXVel;
				
				relativeX = x - bullets[bulletCount].getLocation().x;
				relativeY = y - bullets[bulletCount].getLocation().y;
				
				pathOffset = (relativeY - relativeX * bulletYOverX)/(bulletXVel + bulletYVel * bulletYOverX) + 0.001f;
				pathDistance = relativeX/bulletXVel + pathOffset * bulletYOverX + 0.001f;
				
				if (pathDistance > -0.2 && pathDistance < 2.2)
				{
					cubed = (float) Math.pow((pathOffset + Math.copySign(0.22, pathOffset)), 3);
					xPres += bulletYVel * -0.012f / cubed;
					yPres += bulletXVel * 0.012f / cubed;
				}
				
				if (pathOffset > -1 && pathOffset < 1)
				{
					cubed = (float) Math.pow((pathDistance + Math.copySign(0.5, pathDistance)), 3);
					xPres += bulletXVel * 0.04f / cubed;
					yPres += bulletYVel * 0.04f / cubed;
				}
			}
			if (destination != null)
			{
				xPres += Math.copySign(0.00, destX - x);
				yPres += Math.copySign(0.00, destY - y);
			}
			
			
			x += pressureMultiplier * xPres;
			y += pressureMultiplier * yPres;
			
			relativeX = x - rc.getLocation().x;
			relativeY = y - rc.getLocation().y;
			if (relativeX * relativeX + relativeY * relativeY > rc.getType().strideRadius * rc.getType().strideRadius)
			{
				angle = Math.atan(relativeY / relativeX);
				if (relativeX < 0)
				{
					angle = Math.PI - angle;
				}
				x = (float) (rc.getLocation().x + rc.getType().strideRadius * Math.cos(angle));
				y = (float) (rc.getLocation().y + rc.getType().strideRadius * Math.sin(angle));
			}
			pressureMultiplier = pressureMultiplier * 0.95f;
			System.out.println("bytes left: " + Clock.getBytecodesLeft());
			System.out.println((Clock.getBytecodesLeft() - 1000) / bullets.length);
		}
		return new MapLocation(x, y);
	}
	
}
