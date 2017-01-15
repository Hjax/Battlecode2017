package pathBot;

import battlecode.common.*;

public class AllyData {
	public int type;
	public MapLocation location;
	public int hp;
	public boolean alive;
	public AllyData(int memoryData) {
		type = Utilities.bitInterval(memoryData, 29, 31);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 11, 28));
		hp = Utilities.bitInterval(memoryData, 2, 10);
		alive = Utilities.bitInterval(memoryData, 1, 1) == 1;
	}
	public AllyData(int t, MapLocation loc, int health, boolean is_alive){
		type = t;
		location = loc;
		hp = health;
		alive = is_alive;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 29) + (int) Utilities.targetToInt(location) * (int) Math.pow(2, 11) + hp * (int) Math.pow(2, 2) + ((alive) ? 1 : 0) * 2;
	}
}
