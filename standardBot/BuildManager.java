package standardBot;

import battlecode.common.*;

public class BuildManager extends Bot{
	private static boolean stuck = false;
	public static int UNDECIDED = 0;
	public static int STANDARD = 1;
	public static int AGGRESSIVE = 2;

	public static boolean isStuck() {
		Direction angle = new Direction(0);
		for (int i = 0; i < 72; i++) {
			if (rc.canMove(angle)) {
				return false;
			}
			angle.rotateLeftDegrees(5);
		}
		return true;
	}
	
	public static void updateStuck() throws Exception {
		if (isStuck() && !stuck) {
			stuck = true;
			if (UnitType.getType() == UnitType.ARCHON) {
				Globals.setStuckArchons(Globals.getStuckArchons() + 1);
			}
		}
		else if (!isStuck() && stuck) {
			stuck = false;
			if (UnitType.getType() == UnitType.ARCHON) {
				Globals.setStuckArchons(Globals.getStuckArchons() - 1);
			}
		}
	}
	
	public static void decideBuild() throws Exception {
     	if (enemyArchons.length == 1 && rc.getLocation().distanceTo(enemyArchons[0]) < 32 && Utilities.getDensity() < 0.5)
     	{
     		Globals.setStrat(AGGRESSIVE);
     	} else {
     		Globals.setStrat(STANDARD);
     	}
	}
	
}
