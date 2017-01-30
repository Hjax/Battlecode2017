package frozenStandard;

import battlecode.common.MapLocation;

public class Order {
	public int type;
	public MapLocation location;
	public int TTL;
	public int index;
	public Order(long memoryData, int i) {
		type = Utilities.bitInterval(memoryData, 30, 32);
		TTL = Utilities.bitInterval(memoryData, 18, 29);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 0, 17));
		index = i;

	}
	public Order(int t, MapLocation loc, int time, int i){
		type = t;
		location = loc;
		TTL = time;
		index = i;
	}
	
	public long toLong(){
		// you could hard code the values here for a small bytecode improvement (3 per int, so 6 here)
		return type * (int) Math.pow(2, 30) + TTL * (int) Math.pow(2, 18) + (int) Utilities.targetToInt(location);
	}

	public static int getTTL(long input){
		return Utilities.bitInterval(input, 18, 29);
	}
	
	public static MapLocation getLocation(long input) {
		return Utilities.intToTarget(Utilities.bitInterval(input, 0, 17));
	}
	
	public static int getType(long input) {
		return Utilities.bitInterval(input, 30, 32);
	}
}
