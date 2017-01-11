package first_bot;

import battlecode.common.*;

public class EnemyData {
	public int type;
	public MapLocation location;
	public int hp;
	public boolean building;
	public EnemyData(int memoryData) {
		type = Tools.bitInterval(memoryData, 29, 31);
		location = Tools.intToTarget(Tools.bitInterval(memoryData, 11, 28));
		hp = Tools.bitInterval(memoryData, 2, 10);
		building = Tools.bitInterval(memoryData, 1, 1) == 1;
	}
	public int toInt(){
		return type * (int) Math.pow(2, 29) + (int) Math.round(2 * location.x) * (int) Math.pow(2, 11) + hp;
	}
}
