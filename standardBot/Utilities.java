package standardBot;

import battlecode.common.*;

public class Utilities extends Bot{

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException 
    {    	
        return tryMove(dir,1,20);
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
    	Debug.debug_bytecode_start();
    	float distance = rc.getType().strideRadius;
        // First, try intended direction
        if (rc.canMove(dir) && rc.senseNearbyBullets(rc.getLocation().add(dir, distance), rc.getType().bodyRadius).length == 0) 
        {
        	Bot.lastPosition = rc.getLocation();
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;
        Direction tryLeft = dir;
        Direction tryRight = dir;
        
        while (distance >= 0.1f)
        {

        	while(currentCheck<=checksPerSide) 
        	{
        		// Try the offset of the left side
        		tryLeft = tryLeft.rotateLeftDegrees(degreeOffset);
        		if(rc.canMove(tryLeft) && rc.senseNearbyBullets(rc.getLocation().add(tryLeft, distance), rc.getType().bodyRadius).length == 0) 
        		{
        			Bot.lastPosition = rc.getLocation();
        			rc.move(tryLeft);
        			Debug.debug_bytecode_end("moving");
        			return true;
        		}
        		// Try the offset on the right side
        		tryRight = tryRight.rotateRightDegrees(degreeOffset);
        		if(rc.canMove(tryRight) && rc.senseNearbyBullets(rc.getLocation().add(tryRight, distance), rc.getType().bodyRadius).length == 0) 
        		{
        			Bot.lastPosition = rc.getLocation();
        			rc.move(tryRight);
        			Debug.debug_bytecode_end("moving");
        			return true;
        		}
        		// No move performed, try slightly further
        		currentCheck++;
        		degreeOffset = degreeOffset * 1.15f;
        	}
        	distance -= 0.4f;
        	currentCheck = 1;
        }

        // A move never happened, so return false.
        Debug.debug_bytecode_end("moving");
        return false;
    }

    static boolean willHitAlly(MapLocation target) throws GameActionException 
    {
    	
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
            	Debug.debug_print("hitting ally");
            	return true;
            }
    	}
    	TreeInfo[] alliedTrees = rc.senseNearbyTrees(rc.getLocation().add(rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target) / 2), rc.getLocation().distanceTo(target) / 2, ally);
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
            	Debug.debug_print("hitting allied tree");

					rc.setIndicatorDot(friend.getLocation(), 0, 0, 0);

            	return true;
            }
    	}
    	
    	TreeInfo[] neutralTrees = rc.senseNearbyTrees(rc.getLocation().add(rc.getLocation().directionTo(target), rc.getLocation().distanceTo(target) / 2), rc.getLocation().distanceTo(target) / 2, Team.NEUTRAL);
    	for (TreeInfo friend: neutralTrees) {
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
            	Debug.debug_print("hitting neutral tree");

					rc.setIndicatorDot(friend.getLocation(), 255, 255, 255);

            	return true;
            }
    	}
    	
    	
    	return false;
    }
    
    public static boolean canMoveInto(BulletInfo bullet)
    {
    	
    	float checkRadius = rc.getType().bodyRadius + rc.getType().strideRadius;
    	
    	//clean out false positives preemptively
    	 Direction directionToRobot = bullet.getLocation().directionTo(rc.getLocation());
         float distToRobot = bullet.getLocation().distanceTo(rc.getLocation());
         float theta = bullet.dir.radiansBetween(directionToRobot);

         if (Math.abs(theta) > Math.PI/2 && distToRobot > checkRadius) {
             return false;
         }
    	
    	
    	
    	//math courtesy of wolfram
    	float x1 = bullet.getLocation().x - rc.getLocation().x;
    	float y1 = bullet.getLocation().y - rc.getLocation().y;
    	float x2 = (float) (bullet.getLocation().x + Math.cos(bullet.dir.radians) * bullet.speed) - rc.getLocation().x;
    	float y2 = (float) (bullet.getLocation().y + Math.sin(bullet.dir.radians) * bullet.speed) - rc.getLocation().y;
    	float dx = x2 - x1;
    	float dy = y2 - y1;
    	float D = x1 * y2 - x2 * y1;
    	
    	float discriminant = checkRadius * checkRadius * (dx * dx + dy * dy) - D * D;
    	if (discriminant < 0)
    	{
    		return false;
    	}
    	else
    	{
    		return true;
    	}
    }
    
	public static MapLocation intToTarget(int location){
		// so we have 32 bits in a java int 
		// the high 9 bits are the x, we can get them by dividing
		// the low  9 bits are the y, we can get them by mod
		//return new MapLocation(location / 1024, location % 1024); 
		int x = location / 512;
		int y = location % 512;
		x = ((x / 2) - 100) + Math.round(allyArchons[0].x);
		y = ((y / 2) - 100) + Math.round(allyArchons[0].y);
		return new MapLocation(x, y);
	}
	public static int targetToInt(MapLocation target) {
		int x = ((Math.round(target.x) - Math.round(allyArchons[0].x)) + 100) * 2;
		int y = ((Math.round(target.y) - Math.round(allyArchons[0].y)) + 100) * 2;
		return x * 512 + y;
	}
	
	public static MapLocation getAdjustedMapLocation(MapLocation target) {
		return new MapLocation(((Math.round(target.x) - Math.round(allyArchons[0].x)) + 100) * 2, ((Math.round(target.y) - Math.round(allyArchons[0].y)) + 100) * 2);
	}
	
	public static MapLocation getActualMapLocation(MapLocation adjusted) {
		return new MapLocation(((adjusted.x / 2) - 100) + Math.round(allyArchons[0].x), ((adjusted.y / 2) - 100) + Math.round(allyArchons[0].y));
	}
	
	
	// returns the closest edge in a given direction, -1 otherwise 
	public static float edgeInDirection(Direction dir) throws GameActionException {
		float max_dist = rc.getType().sensorRadius - 0.01f;
		if (rc.onTheMap(rc.getLocation().add(dir, max_dist))){
			return -1;
		}
		float resolution = rc.getType().sensorRadius / 2;
		max_dist -= resolution;
		while (resolution > 0.125) {
			resolution /= 2;
			if (rc.onTheMap(rc.getLocation().add(dir, max_dist))){
				max_dist += resolution;
			} else {
				max_dist -= resolution;
			}
		}
		if (Math.round(dir.getAngleDegrees()) == Math.round(Direction.getNorth().getAngleDegrees()) ||
				Math.round(dir.getAngleDegrees()) == Math.round(Direction.getSouth().getAngleDegrees())) {
			Debug.debug_print(max_dist);
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
				Bot.lastPosition = rc.getLocation();
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
				return;
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
	
	public static MapLocation magnitudePressureDodge(BulletInfo[] allBullets, MapLocation destination)
	{
		Debug.debug_print("STARTING DODGE BYTECODES LEFT: " + Clock.getBytecodesLeft());
		Debug.debug_print("BULLETS PASSED: " + allBullets.length);
		
		BulletInfo[] bullets = new BulletInfo[allBullets.length];
		int relevantBullets = 0;
		
		for (int countBullets = 0; countBullets < allBullets.length; countBullets++)
		{
			if (canMoveInto(allBullets[countBullets]))
			{
				bullets[relevantBullets++] = allBullets[countBullets];
			}
		}
		
		if (relevantBullets == 0)
		{
			if (destination == null)
			{
				return rc.getLocation();
			}
			else
			{
				return destination;
			}
		}
		
		Debug.debug_print("CLEANUP DONE BYTECODES LEFT: " + Clock.getBytecodesLeft());
		Debug.debug_print("BULLETS LEFT: " + relevantBullets);
		
		
		
		
		
		float destX = 0;
		float destY = 0;
		if (destination != null)
		{
			destX = destination.x;
			destY = destination.y;
		}
		float x = rc.getLocation().x;
		float y = rc.getLocation().y;
		float pressureMultiplier = rc.getType().strideRadius * 0.9f;
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
		while ((Clock.getBytecodesLeft() -2750) / relevantBullets > 150)
		{
			xPres = 0;
			yPres = 0;
			for (int bulletCount = 0; bulletCount < relevantBullets; bulletCount++)
			{
				bulletXVel = (float) (bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].dir.radians));
				bulletYVel = (float) (bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].dir.radians));
				bulletYOverX = bulletYVel / bulletXVel;
				
				relativeX = x - bullets[bulletCount].getLocation().x;
				relativeY = y - bullets[bulletCount].getLocation().y;
				
				pathOffset = (relativeY - relativeX * bulletYOverX)/(bulletXVel + bulletYVel * bulletYOverX) + 0.001f;
				pathDistance = relativeX/bulletXVel + pathOffset * bulletYOverX + 0.001f;
				
				if (pathOffset > -1.2 && pathOffset < 1.2 && pathDistance > -0.5)
				{
					if (pathDistance > 0)
					{
						cubed = (float) Math.pow((pathDistance + Math.copySign(0.4, pathDistance)), 3);
						xPres += bulletXVel * 2.5f / cubed;
						yPres += bulletYVel * 2.5f / cubed;
					}
					
					if (pathOffset > -1 && pathOffset < 1 && pathDistance < 1.5 && Math.abs(pathOffset) > 0.00)
					{
						cubed = (float) Math.pow((pathOffset + Math.copySign(0.4, pathOffset)), 3);
						xPres += bulletYVel * -1 / cubed / (Math.abs(pathDistance) + 0.8);
						yPres += bulletXVel / cubed / (Math.abs(pathDistance) + 0.8);
					}
				}
				
				
			}
			if (destination != null)
			{
				xPres += Math.copySign(0.1, destX - x);
				yPres += Math.copySign(0.1, destY - y);
			}
			if (xPres != 0 || yPres != 0)
			{
				angle = Math.atan(yPres / xPres);
				if (xPres < 0)
				{
					angle = Math.PI - angle;
				}
				x += pressureMultiplier * Math.cos(angle);
				y += pressureMultiplier * Math.sin(angle);
				
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
				pressureMultiplier = pressureMultiplier * 0.6f;
			}
			else
			{
				Debug.debug_print("returning early: " + Clock.getBytecodesLeft());
				return (new MapLocation(x,y));
			}
			
			
			Debug.debug_print("bytes left: " + Clock.getBytecodesLeft());
			Debug.debug_print((Clock.getBytecodesLeft() - 1000) / bullets.length);
		}
		return new MapLocation(x, y);
	}

	static void waterTrees(MapLocation roost) throws GameActionException
	{
		Debug.debug_bytecode_start();
		TreeInfo[] trees = rc.senseNearbyTrees(2.0f, ally);
		if (trees.length > 0)
		{
			TreeInfo bestTree = trees[0];
			for (int treeCount = 1; treeCount < trees.length; treeCount++)
			{
				if (trees[treeCount].health < bestTree.health)
				{bestTree = trees[treeCount];}
			}
			try {
				rc.setIndicatorDot(bestTree.getLocation(), 255, 0, 0);
				rc.water(bestTree.ID);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
			
		if (!rc.hasMoved())
		{
			trees = rc.senseNearbyTrees(roost, 3, ally);
			if (trees.length > 0)
			{
				TreeInfo bestTree = trees[0];
				for (int treeCount = 1; treeCount < trees.length; treeCount++)
				{
					if (trees[treeCount].health < bestTree.health)
					{bestTree = trees[treeCount];}
				}
				rc.setIndicatorDot(bestTree.getLocation(), 0, 255, 0);
				moveTo(melee(bestTree.getLocation(), 2.01f));
			}
				
		}
		Debug.debug_bytecode_end("watering");
	}
}
