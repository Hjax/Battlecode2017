package gardenBot;

import battlecode.common.MapLocation;

public class Order {
	public int type;
	public MapLocation location;
	public int TTL;
	public int count;
	public Order(int memoryData) {
		type = Utilities.bitInterval(memoryData, 29, 32);
		TTL = Utilities.bitInterval(memoryData, 24, 28);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 6, 23));
		count = Utilities.bitInterval(memoryData, 0, 5);
	}
	public Order(int t, MapLocation loc, int time, int number){
		type = t;
		location = loc;
		TTL = time;
		count = number;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 29) + TTL * (int) Math.pow(2, 24) + (int) Utilities.targetToInt(location) * (int) Math.pow(2, 6) + count;
	}
}
