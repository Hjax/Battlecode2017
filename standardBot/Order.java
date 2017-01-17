package standardBot;

import battlecode.common.MapLocation;

public class Order {
	public int type;
	public MapLocation location;
	public int TTL;
	public int count;
	public Order(int memoryData) {
		type = Utilities.bitInterval(memoryData, 29, 32);
		TTL = Utilities.bitInterval(memoryData, 23, 28);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 5, 22));
		count = Utilities.bitInterval(memoryData, 0, 4);
	}
	public Order(int t, MapLocation loc, int time, int number){
		type = t;
		location = loc;
		TTL = time;
		count = number;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 29) + TTL * (int) Math.pow(2, 23) + (int) Utilities.targetToInt(location) * (int) Math.pow(2, 5) + count;
	}
	public static int getCount(int input){
		return Utilities.bitInterval(input, 0, 4);
	}
	public static int getTTL(int input){
		return Utilities.bitInterval(input, 23, 28);
	}
}
