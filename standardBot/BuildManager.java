package standardBot;

import battlecode.common.*;

public class BuildManager extends Bot{
	private static boolean stuck = false;
	public static int UNDECIDED = 0;
	public static int STANDARD = 1;
	public static int AGGRESSIVE = 2;
	private static int treeCount = 0;
	private static float density = 0;
	
	private static float unitRatio() throws GameActionException {
		if (Globals.getStrat() == 2) {
			return 2.0f;
		}
		return 0.2f;
	}

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
	
	public static void update() throws Exception {
		treeCount = rc.getTreeCount();
		density = getDensity();
		boolean is_stuck = isStuck();
		if (is_stuck && !stuck) {
			stuck = true;
			if (UnitType.getType() == UnitType.ARCHON) {
				Globals.setStuckArchons(Globals.getStuckArchons() + 1);
			}
		}
		else if (!is_stuck && stuck) {
			stuck = false;
			if (UnitType.getType() == UnitType.ARCHON) {
				Globals.setStuckArchons(Globals.getStuckArchons() - 1);
			}
		}
	}
	
	
	public static void decideBuild() throws Exception {
     	if (enemyArchons.length == 1 && rc.getLocation().distanceTo(enemyArchons[0]) < 32 && density < 0.5)
     	{
     		Globals.setStrat(AGGRESSIVE);
     	} else {
     		Globals.setStrat(STANDARD);
     	}
	}
	
	public static void executeBuild() throws GameActionException {
		if (shouldBuildUnit()) {
			buildNextUnit();
		}
		else {
			if (rc.getTeamBullets() >= 50 && rc.isBuildReady()) {
				Debug.debug_print("Trying to plant a tree");
				plantSpacedTree(Gardener.roost);
			}
		}
	}
	
	public static boolean shouldBuildUnit() throws GameActionException {
		return (treeCount < 5 && Globals.getUnitCount(UnitType.SOLDIER) < treeCount / 2.0) || (Globals.getUnitCount(UnitType.SOLDIER) < (treeCount * unitRatio())) ||
				(density > 0.4 || (rc.getTreeCount() >= 20 && Globals.getUnitCount(UnitType.LUMBERJACK) < treeCount / 7));
	}
	
	public static void buildNextUnit() throws GameActionException { 
		if (BuildManager.getDensity() > 0.4 || (rc.getTreeCount() >= 20 && Globals.getUnitCount(UnitType.LUMBERJACK) < treeCount / 7) && (Globals.getStrat() != AGGRESSIVE | rc.getRoundNum() > 350)){
			Debug.debug_print("Trying to build Lumberjack");
			trainUnit(RobotType.LUMBERJACK);
		} else {
			Debug.debug_print("Trying to build Soldier");
			trainUnit(RobotType.SOLDIER);
		}
	}
	
	public static void checkDonateVP() throws GameActionException {
    	int win_round = Math.min(100, rc.getRoundLimit() - rc.getRoundNum());
    	// averaged over the rounds we want to win 
    	float victory_point_cost = (float) ((7.5 + (rc.getRoundNum() * 12.5 / 3000)) + (7.5 + ((rc.getRoundNum() + win_round)* 12.5 / 3000))) / 2;
    	float bullets_to_win = (GameConstants.VICTORY_POINTS_TO_WIN - rc.getTeamVictoryPoints()) * victory_point_cost;
    	if (rc.getTreeCount() > 0) {
    		Debug.debug_print("Winning from vp in " + Float.toString((bullets_to_win - rc.getTeamBullets()) / rc.getTreeCount()));
    		Debug.debug_print("Needed bullets: " + Float.toString(bullets_to_win));
    		Debug.debug_print("Current vp cost: " + Float.toString(victory_point_cost));
    	}

    	// donate bullets if we can win in 100 rounds, or if we have 1000 bullets, or if theres 150 rounds or less until the end of the game
    	if ((rc.getTreeCount() > 0 && ((bullets_to_win - rc.getTeamBullets()) / rc.getTreeCount()) <= win_round) || 
    		(rc.getTeamBullets() > 1000 || rc.getRoundLimit() - rc.getRoundNum() < 150))  {
    		rc.donate((float) (rc.getTeamBullets() - rc.getTeamBullets() % (7.5 + (rc.getRoundNum() * 12.5 / 3000))));
    	}
	}

	public static void trainUnit(RobotType unit) throws GameActionException
	{
		Direction angle = new Direction(0);
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
		if (unit == RobotType.LUMBERJACK && nearbyTrees.length > 0)
		{
			angle = rc.getLocation().directionTo(nearbyTrees[0].getLocation()); 
		} else if (unit == RobotType.SOLDIER && Globals.getOrderCount() > 0) {
			angle = rc.getLocation().directionTo(Order.getLocation(Memory.getOrder(0)));
		}
		int turnCount = 0;
		Direction testAngle = angle;
		while (!rc.canBuildRobot(unit, testAngle) && turnCount++ < 60)
		{
			testAngle = angle.rotateRightDegrees(3 * turnCount);
			if (!rc.canBuildRobot(unit, testAngle))
			{
				testAngle = angle.rotateLeftDegrees(3 * turnCount);
			}
		}
		try {
			if (rc.canBuildRobot(unit,  testAngle))
				{rc.buildRobot(unit, testAngle);}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static float getDensity() throws GameActionException{
		Debug.debug_bytecode_start();
		float total = 0;
		TreeInfo blockingTree = null;
		for (float i = 0; i < 2 * Math.PI; i += Math.PI / 6) {
			MapLocation clone = rc.getLocation().add(i, rc.getType().bodyRadius - 0.01f);
			for (int j = 1; j <= 5; j++) 
			{
				clone = clone.add(i, (rc.getType().sensorRadius - rc.getType().bodyRadius) / 5);
				blockingTree = rc.senseTreeAtLocation(clone);
				if (!rc.onTheMap(clone) || (blockingTree != null && blockingTree.getTeam() != rc.getTeam())) 
				{ 
					if (!rc.onTheMap(clone))
					{
						total += (6 - j)/180f;
					}
					else
					{
						total += (6 - j)/60f;
					}
					
	
					break;
				}
			}
		}
		Debug.debug_bytecode_end("Tree density");
		Debug.debug_print("density is: " + total);
		return total;
	}

	private static boolean plantSpacedTree(MapLocation roost) throws GameActionException
	{
		Debug.debug_bytecode_start();
		Direction angle = new Direction(0);
		int turnCount = 0;
		Debug.debug_print("trying to plant");
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(angle, 3.0f), 1.05f) || (Globals.getUnitCount(UnitType.GARDENER) < 2 && (rc.senseNearbyTrees(roost.add(angle, 3.0f), 1.05f, ally).length + rc.senseNearbyTrees(roost.add(angle, 3.0f), 2.5f, Team.NEUTRAL).length > 0 || !rc.onTheMap(roost.add(angle, 3.0f), 2.5f))) || !rc.onTheMap(roost.add(angle, 3.0f), 1.05f)) && turnCount++ < 8)
		{
			rc.setIndicatorDot(roost.add(angle, 3.0f), 155, 155, 155);
			
			angle = angle.rotateLeftDegrees(45);
		}
		try 
		{
			if (turnCount < 8)
			{
				if (!rc.hasMoved())
				{
					Debug.debug_print("moving to plant");
					Utilities.moveTo((roost.add(angle, 2.0f)));
				}
				else
				{
					Debug.debug_print("already moved, can't plant");
				}
				if (rc.getLocation().distanceTo(roost.add(angle, 3.0f)) <= 2.0f)
				{
					Gardener.buildIndex++;
					Debug.debug_print("Planting");
					rc.plantTree(rc.getLocation().directionTo(roost.add(angle, 3.0f)));
				}
				
				return true;
			}
			else
			{
				Debug.debug_print("can't plant");
				Gardener.buildIndex++;
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		Debug.debug_bytecode_end("planting");
		return false;
	}
	
	
	
}
