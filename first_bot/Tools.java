package first_bot;

import battlecode.common.*;

public class Tools {
	public static int[] getGridTarget(MapLocation target){
		// so we have a 200x200 targeting grid, we want to closest grid point to a target
		int[] coords = new int[2];
		coords[0] = Math.round(target.x * 2);
		coords[1] = Math.round(target.y * 2);
		return coords;
	}
	public static MapLocation intToTarget(int location){
		// so we have 32 bits in a java int 
		// the high 8 bits are the x, we can get them by dividing
		// the low  8 bits are the y, we can get them by mod
		return new MapLocation(location / 256, location % 256); 
	}
	public static int targetToInt(MapLocation target) {
		return Math.round(target.x *  256 + target.y);
	}
	
	public static int bitInterval(int input, int start, int end) {
		// found this equation on stackoverflow
		return (input >> start) & ~(~0 << (end-start+1));
	}
	
}
