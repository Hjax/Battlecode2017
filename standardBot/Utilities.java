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
        return tryMove(dir,2.5f,20);
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
    	
    	rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(dir, distance + rc.getType().bodyRadius), 0, 255, 0);
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
        
        while (currentCheck<=checksPerSide && Clock.getBytecodesLeft() > 750)
        {
        	// Try the offset of the left side
    		tryLeft = tryLeft.rotateLeftDegrees(degreeOffset);
    		rc.setIndicatorDot(rc.getLocation().add(tryLeft, distance + rc.getType().bodyRadius), 0, 0, 255);
			while (distance >= 0.1f)
			{
				if(rc.canMove(tryLeft, distance)) 
	    		{
					Bot.lastPosition = rc.getLocation();
					rc.move(tryLeft, distance);
					Debug.debug_bytecode_end("moving");
					return true;
	    		}
				distance -= 0.4f;
			}
    		distance = rc.getType().strideRadius; 			
    		// Try the offset on the right side
    		tryRight = tryRight.rotateRightDegrees(degreeOffset);
    		rc.setIndicatorDot(rc.getLocation().add(tryRight, distance + rc.getType().bodyRadius), 0, 0, 255);
    		while (distance >= 0.1f)
			{
    			if(rc.canMove(tryRight, distance)) 
        		{
    				Bot.lastPosition = rc.getLocation();
    				rc.move(tryRight, distance);
    				Debug.debug_bytecode_end("moving");
    				return true;
        		}
    			distance -= 0.35f;
				
			}
    		distance = rc.getType().strideRadius;	
    		// No move performed, try slightly further
    		currentCheck++;
    		degreeOffset = degreeOffset * 1.10f;
        }

        // A move never happened, so return false.
        Debug.debug_bytecode_end("moving");
        Bot.lastPosition = rc.getLocation();
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

            if (perpendicularDist < friend.radius) {
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
			
			if (distance <= speed && rc.canMove(destination))
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
	
	public static MapLocation magnitudePressureDodge(BulletInfo[] bullets) throws GameActionException
	{
		return magnitudePressureDodge(bullets, null);
	}
	
	public static MapLocation magnitudePressureDodge(BulletInfo[] allBullets, MapLocation destination) throws GameActionException
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
		float relativeX = 0f;
		float relativeY = 0f;
		float pathOffset = 0f;
		float pathDistance = 0f;
		float cubed = 0f;
		double angle = 0;
		float[][] bulletVels = new float[relevantBullets][3]; //0 = x, 1 = y, 2 = y/x
		for (int bulletCount = 0; bulletCount < relevantBullets; bulletCount++)
		{
			bulletVels[bulletCount][0] = (float) (bullets[bulletCount].getSpeed() * Math.cos(bullets[bulletCount].dir.radians));
			bulletVels[bulletCount][1] = (float) (bullets[bulletCount].getSpeed() * Math.sin(bullets[bulletCount].dir.radians));
			bulletVels[bulletCount][2] = (float) Math.tan(bullets[bulletCount].dir.radians);
		}
		
		while ((Clock.getBytecodesLeft() -3000) / relevantBullets > 150)
		{
			xPres = 0;
			yPres = 0;
			for (int bulletCount = 0; bulletCount < relevantBullets; bulletCount++)
			{				
				relativeX = x - bullets[bulletCount].getLocation().x;
				relativeY = y - bullets[bulletCount].getLocation().y;
				
				pathOffset = (relativeY - relativeX * bulletVels[bulletCount][2])/(bulletVels[bulletCount][0] + bulletVels[bulletCount][1] * bulletVels[bulletCount][2]) + 0.001f;
				pathDistance = relativeX/bulletVels[bulletCount][0] + pathOffset * bulletVels[bulletCount][2] + 0.001f;
				
				if (pathOffset > -2 && pathOffset < 2 && pathDistance > 0)
				{
					if (pathDistance > 0)
					{
						cubed = (float) Math.pow((pathDistance + Math.copySign(0.3, pathDistance)), 3);
						xPres += bulletVels[bulletCount][0] * 36f / cubed;
						yPres += bulletVels[bulletCount][1] * 36f / cubed;
					}
					
					if (pathOffset > -1.5 && pathOffset < 1.5 && pathDistance < 5 && (Math.abs(pathOffset) > 0.1 || pathDistance > 1))
					{
						cubed = (float) Math.pow((pathOffset + Math.copySign(0.1, pathOffset)), 3);
						xPres += bulletVels[bulletCount][1] * -1 / cubed;
						yPres += bulletVels[bulletCount][0] * 1 / cubed;
					}
				}
				
				
			}
			if (destination != null)
			{
				xPres += Math.copySign(0.05, destX - x);
				yPres += Math.copySign(0.05, destY - y);
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
				rc.setIndicatorDot(new MapLocation(x,y), 133, 133, 133);
			}
			else
			{
				Debug.debug_print("returning early: " + Clock.getBytecodesLeft());
				return (new MapLocation(x,y));
			}
			
			
			Debug.debug_print("bytes left: " + Clock.getBytecodesLeft());
			Debug.debug_print((Clock.getBytecodesLeft() - 3000) / bullets.length);
		}
		return new MapLocation(x, y);
	}

	static void waterTrees(MapLocation roost) throws GameActionException
	{
		Debug.debug_bytecode_start();
		TreeInfo[] trees = rc.senseNearbyTrees(2.0f, ally);
		RobotInfo[] archons = null;
		int unit = 0;
		if (trees.length > 0)
		{
			TreeInfo bestTree = null;
			for (int treeCount = 0; treeCount < trees.length; treeCount++)
			{
				if (bestTree == null || trees[treeCount].health < bestTree.health)
				{
					archons = rc.senseNearbyRobots(trees[treeCount].location, 2f, ally);
					for (unit = 0; unit < archons.length; unit++)
					{
						if (archons[unit].getType() == RobotType.ARCHON || archons[unit].getType() == RobotType.TANK)
						{
							Debug.debug_print("LETTING TREE DIE");
							break;
						}
					}
					if (unit >= archons.length)
					{
						bestTree = trees[treeCount];
					}
					
				}
			}
			try {
				if (bestTree != null)
				{
					rc.setIndicatorDot(bestTree.getLocation(), 255, 0, 0);
					rc.water(bestTree.ID);
				}
				
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
			
		if (false && !rc.hasMoved())
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
	
	public static MapLocation naivePressureDodge(BulletInfo[] allBullets, MapLocation destination) throws GameActionException
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
		Debug.debug_bytecode_start();
		float pressureMultiplier = rc.getType().strideRadius * 0.9f;
		float xPres = 0f;
		float yPres = 0f;
		float relativeX = 0f;
		float relativeY = 0f;
		MapLocation bulletLoc = null;
		MapLocation currentLoc = rc.getLocation();
		TreeInfo treeCollision = null;
		TreeInfo[] trees = rc.senseNearbyTrees(rc.getType().bodyRadius + rc.getType().strideRadius);
		
		while (Clock.getBytecodesLeft() >  3500)
		{
			xPres = 0;
			yPres = 0;
			//dodge bullets
			for (int countBullets = 0; countBullets < relevantBullets; countBullets++)
			{
				if (Clock.getBytecodesLeft() > 3500)
				{
					
					bulletLoc = bullets[countBullets].location;
					relativeX = currentLoc.x - bulletLoc.x;
					relativeX += Math.copySign(0.5, relativeX);
					relativeY = currentLoc.y - bulletLoc.y;
					relativeY += Math.copySign(0.5, relativeY);
					xPres += relativeX / ((currentLoc.distanceTo(bulletLoc) + 0.05) * (currentLoc.distanceTo(bulletLoc) + 0.05));
					yPres += relativeY / ((currentLoc.distanceTo(bulletLoc) + 0.05) * (currentLoc.distanceTo(bulletLoc) + 0.05));
					for (int bulletStep = 0; bulletStep < 5; bulletStep++)
					{
						treeCollision = rc.senseTreeAtLocation(bulletLoc);
						if (treeCollision != null)
						{
							break;
						}
						bulletLoc = bulletLoc.add(bullets[countBullets].dir, bullets[countBullets].speed / 4);
						relativeX = currentLoc.x - bulletLoc.x;
						relativeX += Math.copySign(0.5, relativeX);
						relativeY = currentLoc.y - bulletLoc.y;
						relativeY += Math.copySign(0.5, relativeY);
						xPres += relativeX / ((currentLoc.distanceTo(bulletLoc) + 0.05) * (currentLoc.distanceTo(bulletLoc) + 0.05));
						yPres += relativeY / ((currentLoc.distanceTo(bulletLoc) + 0.05) * (currentLoc.distanceTo(bulletLoc) + 0.05));
					}
				}
				else
				{
					break;
				}
				//avoid trees if not scout
				if (rc.getType() != RobotType.SCOUT)
				{
					for (int countTrees = 0; countTrees < trees.length; countTrees++)
					{
						relativeX = currentLoc.x - trees[countTrees].location.x;
						relativeX += Math.copySign(0.5, relativeX);
						relativeY = currentLoc.y - trees[countTrees].location.y;
						relativeY += Math.copySign(0.5, relativeY);
						xPres += relativeX * 2 / ((currentLoc.distanceTo(trees[countTrees].location) - trees[countTrees].radius + 0.05) * (currentLoc.distanceTo(trees[countTrees].location) - trees[countTrees].radius + 0.05));
						yPres += relativeY * 2 / ((currentLoc.distanceTo(trees[countTrees].location) - trees[countTrees].radius + 0.05) * (currentLoc.distanceTo(trees[countTrees].location) - trees[countTrees].radius + 0.05));
					}
				}
			}
			if (destination != null)
			{
				xPres += Math.copySign(0.05, destination.x - currentLoc.x);
				yPres += Math.copySign(0.05, destination.y - currentLoc.y);
			}
			

	    	if (Globals.getTopEdge() != -1)
	    	{
	    		relativeY = Globals.getTopEdge() - rc.getLocation().y;
	    		yPres += -10/(relativeY * relativeY);
	    	}
	    	if (Globals.getBottomEdge() != -1)
	    	{
	    		relativeY = rc.getLocation().y - Globals.getBottomEdge();
	    		yPres += 10/(relativeY * relativeY);
	    	}
	    		
	    	if (Globals.getRightEdge() != -1)
	    	{
	    		relativeX = Globals.getRightEdge() - rc.getLocation().x;
	    		xPres += -10/(relativeX * relativeX);	
	    	}
	    	if (Globals.getLeftEdge() != -1)
	    	{
	    		relativeX = rc.getLocation().x - Globals.getLeftEdge();
	    		xPres += 10/(relativeX * relativeX);
	    	}
			
			currentLoc = currentLoc.add(new Direction(xPres, yPres), pressureMultiplier);
			
			if (rc.getLocation().distanceTo(currentLoc) > rc.getType().strideRadius)
			{
				currentLoc = rc.getLocation().add(rc.getLocation().directionTo(currentLoc), rc.getType().strideRadius);
			}
			pressureMultiplier *= 0.6;
			Debug.debug_print("bytes left: " + Clock.getBytecodesLeft());
			Debug.debug_print((Clock.getBytecodesLeft() - 3500) / bullets.length);
		}
		Debug.debug_bytecode_end("naive dodging ");
		return currentLoc;
		
		
	}
	
	public static int[] symmetrizeStarts(MapLocation[] ours, MapLocation[] theirs)
	{
		int[][] symmetryType = new int[ours.length][theirs.length];
		// 0 = x symmetry
		// 1 = y symmetry
		// 2 = rotational symmetry
		// -1 = no symmetry
		int[] symmetryCounts = new int[3];
		int decidedSymmetry = -1;
		int[] results = new int[ours.length];
		float[][] xMid = new float[ours.length][theirs.length];
		float[][] yMid = new float[ours.length][theirs.length];
		
		for (int allyArchon = 0; allyArchon < ours.length; allyArchon++)
		{
			for (int enemyArchon = 0; enemyArchon < theirs.length; enemyArchon++)
			{
				xMid[allyArchon][enemyArchon] = (ours[allyArchon].x + theirs[enemyArchon].x)/2; 
				yMid[allyArchon][enemyArchon] = (ours[allyArchon].y + theirs[enemyArchon].y)/2;
				if (Math.abs(ours[allyArchon].x - theirs[enemyArchon].x) < 0.01f)
				{
					symmetryType[allyArchon][enemyArchon] = 1;
					symmetryCounts[1]++;
				}
				else if (Math.abs(ours[allyArchon].y - theirs[enemyArchon].y) < 0.01f)
				{
					symmetryType[allyArchon][enemyArchon] = 0;
					symmetryCounts[0]++;
					
				}
				else symmetryType[allyArchon][enemyArchon] = -1;
			}
		}
		if (symmetryCounts[0] >= ours.length)
		{
			decidedSymmetry = 0;
		}
		else if (symmetryCounts[1] >= ours.length)
		{
			decidedSymmetry = 1;

		}
		else
		{
			decidedSymmetry = 2;
		}
		Debug.debug_print("Symmetry type: " + decidedSymmetry);
		float xMidCheck = 0;
		float yMidCheck = 0;
		for (int checkArchon = 0; checkArchon < ours.length; checkArchon++)
		{
			xMidCheck = xMid[0][checkArchon];
			yMidCheck = yMid[0][checkArchon];
			for (int allyArchon = 0; allyArchon < ours.length; allyArchon++)
			{
				for (int enemyArchon = 0; enemyArchon < theirs.length; enemyArchon++)
				{
					if (decidedSymmetry == 0)
					{
						if (Math.abs(ours[allyArchon].y - theirs[enemyArchon].y) < 0.01f && Math.abs(xMid[allyArchon][enemyArchon] - xMidCheck) < 0.01f)
						{
							results[enemyArchon] = allyArchon;
							break;
						}
					}
					if (decidedSymmetry == 1)
					{
						if (Math.abs(ours[allyArchon].x - theirs[enemyArchon].x) < 0.01f && Math.abs(yMid[allyArchon][enemyArchon] - yMidCheck) < 0.01f)
						{
							results[enemyArchon] = allyArchon;
							break;
						}
					}
					if (decidedSymmetry == 2)
					{
						if (Math.abs(xMid[allyArchon][enemyArchon] - xMidCheck) < 0.01f && Math.abs(yMid[allyArchon][enemyArchon] - yMidCheck) < 0.01f)
						{
							results[enemyArchon] = allyArchon;
							break;
						}
					}
				}
			}
		}
		return results;
	}
	
	public static MapLocation getAverageEnemyArchonLocation() {
		int i;
		float x = 0;
		float y = 0;
		for (i = 0; i < enemyArchons.length; i++) {
			x += enemyArchons[i].x;
			y += enemyArchons[i].y;
		}
		return new MapLocation(x / i, y / i);
	}
	
}
