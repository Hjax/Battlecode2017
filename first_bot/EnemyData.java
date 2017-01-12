package first_bot;

import battlecode.common.*;

public class EnemyData {
	public int type;
	public MapLocation location;
	public int hp;
	public boolean building;
	public EnemyData(int memoryData) {
		type = Utilities.bitInterval(memoryData, 29, 31);
		location = Utilities.intToTarget(Utilities.bitInterval(memoryData, 11, 28));
		hp = Utilities.bitInterval(memoryData, 2, 10);
		building = Utilities.bitInterval(memoryData, 1, 1) == 1;
	}
	public EnemyData(int t, MapLocation loc, int health, boolean is_building){
		type = t;
		location = loc;
		hp = health;
		building = is_building;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 29) + (int) Utilities.targetToInt(location) * (int) Math.pow(2, 11) + hp * (int) Math.pow(2, 2) + ((building) ? 1 : 0) * 2;
	}
}
