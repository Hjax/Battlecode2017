package first_bot;

import battlecode.common.*;

public class Tools extends Bot{
	
	public static MapLocation intToTarget(int location){
		// so we have 32 bits in a java int 
		// the high 10 bits are the x, we can get them by dividing
		// the low  10 bits are the y, we can get them by mod
		return new MapLocation(location / 1024, location % 1024); 
	}
	public static int targetToInt(MapLocation target) {
		int x = ((Math.round(target.x) - Math.round(archonStart.x)) + 100) * 2;
		int y = ((Math.round(target.y) - Math.round(archonStart.y)) + 100) * 2;
		return x * 1024 + y;
	}
	
	public static int bitInterval(int input, int start, int end) {
		// found this equation on stackoverflow
		return (input >> start) & ~(~0 << (end-start+1));
	}
	
}
