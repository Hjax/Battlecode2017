package standardBot;

import battlecode.common.*;

public class BuildPlanner extends Bot {

	public static boolean isTrapped() {
		for (int i = 0; i < 2 * Math.PI; i += 1/3 * Math.PI) {
			if (rc.canMove(new Direction(i))){
				return false;
			}
		}
		return true;
	}
	
	public static float getDensity() throws GameActionException{
		float total = 0;
		for (int i = 0; i < 2 * Math.PI; i += 1/8 * Math.PI) {
			MapLocation clone = rc.getLocation();
			for (int j = 1; j <= 3; j++) {
				clone.add(i, j * (rc.getType().sensorRadius / 3));
				if (rc.isLocationOccupiedByTree(clone)) { 
					total += 1/48;
				}
			}
		}
		return total;
	}
	
	public static float getDistanceToClosestEnemyArchon(MapLocation m) {
		float best = 99999f;
		for (MapLocation a: enemyArchons) {
			if (rc.getLocation().distanceTo(a) < best) {
				best = rc.getLocation().distanceTo(a);
			}
		}
		return best;
	}
	
	public static void setArchonScore() throws Exception {
		int i;
		for (i = 0; i < 3; i++){
			if (Globals.getArchonScore(i) == 0){
				break;
			}
		}
		if (isTrapped()) {
			Globals.setArchonScore(i, 10002);
		} else {
			Globals.setArchonScore(i, (int) (10000 * getDensity()) + 1);
		}
	}
	
	public static boolean shouldBuild() throws GameActionException {
		if (allyArchons.length == 0) {
			return true;
		}
		int max = 0;
		for (int i = 0; i < allyArchons.length; i++){
			int current_score = Globals.getArchonScore(i);
			if (current_score == 0) {
				return false;
			} else if (current_score >= max && ((i == allyArchons.length - 1) || Globals.getArchonScore(allyArchons.length - 1) != 0)) {
				return true;
			}
			max = Math.max(max, current_score);
		}
		return false;
	}
}
