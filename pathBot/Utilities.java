package pathBot;

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
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,5,16);
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

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;
        float distance = rc.getType().strideRadius;
        
        while (distance >= 0.1f)
        {

        	while(currentCheck<=checksPerSide) 
        	{
        		// Try the offset of the left side
        		if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
        			rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
        			Memory.updateEdges();
        			return true;
        		}
        		// Try the offset on the right side
        		if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
        			rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
        			Memory.updateEdges();
        			return true;
        		}
        		// No move performed, try slightly further
        		currentCheck++;
        	}
        	distance -= 0.2f;
        	currentCheck = 1;
        }

        // A move never happened, so return false.
        return false;
    }

    static boolean willHitAlly(MapLocation bulletLoc, Direction bulletDir, float targetDistance) {
    	
    	RobotInfo[] allies = rc.senseNearbyRobots(targetDistance, ally);
    	for (RobotInfo friend: allies) {
            // Calculate bullet relations to this robot
            Direction directionToRobot = bulletLoc.directionTo(friend.location);
            float distToRobot = bulletLoc.distanceTo(friend.location);
            float theta = bulletDir.radiansBetween(directionToRobot);

            // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
            if (Math.abs(theta) > Math.PI/2) {
                return false;
            }

            // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
            // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
            // This corresponds to the smallest radius circle centered at our location that would intersect with the
            // line that is the path of the bullet.
            float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

            if (perpendicularDist > friend.getType().bodyRadius) {
            	return true;
            }
    	}
    	return false;
    }
    
	public static MapLocation intToTarget(int location){
		// so we have 32 bits in a java int 
		// the high 10 bits are the x, we can get them by dividing
		// the low  10 bits are the y, we can get them by mod
		//return new MapLocation(location / 1024, location % 1024); 
		int x = location / 1024;
		int y = location % 1024;
		x = ((x / 2) - 100) + Math.round(archonStart.x);
		y = ((y / 2) - 100) + Math.round(archonStart.y);
		return new MapLocation(x, y);
	}
	public static int targetToInt(MapLocation target) {
		int x = ((Math.round(target.x) - Math.round(archonStart.x)) + 100) * 2;
		int y = ((Math.round(target.y) - Math.round(archonStart.y)) + 100) * 2;
		return x * 1024 + y;
	}
	
	
	// returns the closest edge in a given direction, -1 otherwise 
	public static float edgeInDirection(Direction dir) throws GameActionException {
		float max_dist = rc.getType().sensorRadius;
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
	
	public static int bitInterval(int input, int start, int end) {
		// found this equation on stackoverflow
		return (input >> start) & ~(~0 << (end-start+1));
	}
	
	public static int typeToNumber(RobotType input) throws Exception {
		// 0 tree
		// 1 soldier
		// 2 tank
		// 3 scout
		// 4 lumberjack
		// 5 gardener
		// 6 archon
		// 7 neural tree
        switch (input) {
        	case ARCHON:
        		return 6;
        	case GARDENER:
        		return 5;
        	case SOLDIER:
        		return 1;
        	case LUMBERJACK:
        		return 4;
        	case TANK:
        		return 2;
        	case SCOUT:
        		return 3;
        	default:
        		throw new Exception("Unexpected Robot Type: " + rc.getType());
        	
        }
	}
	public static RobotType numberToType(int input) throws Exception {
		switch (input) {
	    	case 6:
	    		return RobotType.ARCHON;
	    	case 5:
	    		return RobotType.GARDENER;
	    	case 1:
	    		return RobotType.SOLDIER;
	    	case 4:
	    		return RobotType.LUMBERJACK;
	    	case 2:
	    		return RobotType.TANK;
	    	case 3:
	    		return RobotType.SCOUT;
	    	default:
	    		throw new Exception("Unexpected Robot Type: " + rc.getType());
		}
	}
}
