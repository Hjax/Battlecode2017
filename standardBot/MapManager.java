package standardBot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class MapManager extends Bot {
	
	public static int getGridIndex(MapLocation m) {
		m = Utilities.getAdjustedMapLocation(m);
		int a = (int) Math.floor(m.x / 5);
		int b = (int) Math.floor(m.y / 5);
		return a + 80 * b;
	}
	
	public static MapLocation getGridCorner(MapLocation m) {
		m = Utilities.getAdjustedMapLocation(m);
		int a = (int) (m.x - (m.x % 5));
		int b = (int) (m.y - (m.y % 5));
		return Utilities.getActualMapLocation(new MapLocation(a * 80, b * 80));
	}
	
	// returns an estimate of how full a grid square is
	// top left corner passed as argument
	public static float getFilled(MapLocation m) throws GameActionException {
		float full = 0;
		for (int i = 0; i < 10; i++){
			for (int j = 0; j < 10; j++){
				MapLocation current = new MapLocation((float) (m.x + i * 0.5), (float) (m.y + j * 0.5));
				if (rc.senseTreeAtLocation(current) != null) {
					full += 0.01;
				}
			}
		}
		return full;
	}

}
