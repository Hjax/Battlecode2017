package standardBot;

import battlecode.common.*;

public class AllyData {
	public int type;
	public MapLocation location;
	public int hp;
	public boolean alive;
	public AllyData(int memoryData) {
		type = Utilities.bitInterval(memoryData, 28, 30);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 10, 27));
		hp = Utilities.bitInterval(memoryData, 1, 9);
		// 0 0 is the same as & 1
		alive = (memoryData & 1) == 1;
	}
	public AllyData(int t, MapLocation loc, int health, boolean is_alive){
		type = t;
		location = loc;
		hp = health;
		alive = is_alive;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 28) + (int) Utilities.targetToInt(location) * (int) Math.pow(2, 10) + hp * 2 + ((alive) ? 1 : 0);
	}
	
	public static boolean isAlive(int memoryData){
		//return Utilities.bitInterval(memoryData, 0, 0) == 1;
		// 0 0 is the same as & 1
		return (memoryData & 1) == 1;
	}
	
	public static int getType(int memoryData) {
		return Utilities.bitInterval(memoryData, 28, 30);
	}
	
	public static int getLocInt(int memoryData){
		return Utilities.bitInterval(memoryData, 10, 27);
	}
}
