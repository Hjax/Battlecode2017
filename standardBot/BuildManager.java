package standardBot;

import battlecode.common.*;

public class BuildManager extends Bot{
	private static boolean stuck = false;
	public static int UNDECIDED = 0;
	public static int STANDARD = 1;
	public static int AGGRESSIVE = 2;
	private static int treeCount = 0;
	private static float density = 0;

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
	
	public static void executeBuild() throws Exception {
		if (!buildNextUnit()) {
			if (UnitType.getType() == UnitType.TRAINER){
				return;
				}
			if (rc.getTeamBullets() >= 50 && rc.isBuildReady()) {
				Debug.debug_print("Trying to plant a tree");
				plantSpacedTree(Gardener.roost);
			}
		}
	}
	
	public static boolean shouldBuildLumberjack() throws GameActionException {
		return (density > 0.4 || (rc.getTreeCount() >= 20 && Globals.getUnitCount(UnitType.LUMBERJACK) < treeCount / 7));
	}
	
	public static boolean shouldBuildSoldier() throws GameActionException {
		if (Globals.getStrat() == AGGRESSIVE) {
			return (treeCount <= 35) && ((treeCount < 3 && Globals.getUnitCount(UnitType.SOLDIER) < treeCount) || (Globals.getUnitCount(UnitType.SOLDIER) < ((treeCount + 1) * 2)));
		} else {
			return (treeCount <= 35) && ((treeCount < 3 && Globals.getUnitCount(UnitType.SOLDIER) < treeCount) || (Globals.getUnitCount(UnitType.SOLDIER) < ((treeCount) * 0.3)));
		}
		
	}
	
	public static boolean shouldBuildTank() throws GameActionException {
		return (treeCount > 60 || (treeCount > 35 && Globals.getUnitCount(UnitType.TANK) < (treeCount / 4.0)));
	}
	
	public static boolean shouldBuildScout() throws GameActionException {
		return (Globals.getOrderCount() == 0 && Globals.getUnitCount(UnitType.SCOUT) < 6);
	}
	
	public static boolean shouldBuildGardner() throws GameActionException {
		int gcount = Globals.getUnitCount(UnitType.GARDENER);
		if (Globals.getUnitCount(UnitType.GARDENER) == 1 && Globals.getStrat() == AGGRESSIVE) {
			return false;
		}
		if (rc.getRoundNum() > 30 || gcount == 0){
			if (gcount <= 1) {
				return true;
			} else if (treeCount / gcount >= 2) {
				return true;
			} else if (((float) treeCount / (float) gcount) > 6) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean buildNextUnit() throws Exception { 
		if (rc.isBuildReady()) {
			if (rc.getType() == RobotType.ARCHON) {
				if (shouldBuildGardner() && rc.getTeamBullets() > RobotType.GARDENER.bulletCost) {
					Debug.debug_print("Trying to build gardener");
					trainGardener();
					return true;
				}
				return false;
			}
			
			if (shouldBuildSoldier()) {
				System.out.println("I want to build a soldier");
				if (rc.getTeamBullets() > RobotType.SOLDIER.bulletCost){
					Debug.debug_print("Trying to build SOLDIER");
					trainUnit(RobotType.SOLDIER);
				}
				return true;
			}
			else if (shouldBuildLumberjack()){
				if (rc.getTeamBullets() > RobotType.LUMBERJACK.bulletCost){
					Debug.debug_print("Trying to build lumberjack");
					trainUnit(RobotType.LUMBERJACK);
				}
				return true;

			} else if (shouldBuildTank()) {
				if (rc.getTeamBullets() > RobotType.TANK.bulletCost){
					Debug.debug_print("Trying to build tank");
					trainUnit(RobotType.TANK);
				}
				return true;
			} else if (shouldBuildScout()){
				if (rc.getTeamBullets() > RobotType.SCOUT.bulletCost){
					Debug.debug_print("Trying to build scout");
					trainUnit(RobotType.SCOUT);
				}
				return true;
			}	
		}
		return false;
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

	public static void trainUnit(RobotType unit) throws Exception
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
				Globals.updateUnitCounts(UnitType.getType(unit));
				{rc.buildRobot(unit, testAngle);}
		} catch (GameActionException e) {
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

	private static void trainGardener() throws Exception
	{
		Direction angle = new Direction(0);
		float leftDist, rightDist, topDist, bottomDist;
		leftDist = 1000;
		rightDist = 1000;
		topDist = 1000;
		bottomDist = 1000;
		
		
		
		if (Globals.getLeftEdge() != -1)
		{
			leftDist = rc.getLocation().x - Globals.getLeftEdge();
		}
		if (Globals.getRightEdge() != -1)
		{
			rightDist = Globals.getRightEdge() - rc.getLocation().x;
		}
		if (Globals.getBottomEdge() != -1)
		{
			bottomDist =  rc.getLocation().y - Globals.getBottomEdge();
		}
		if (Globals.getTopEdge() != -1)
		{
			topDist = Globals.getTopEdge() - rc.getLocation().y;
		}
		
		
		if (leftDist < rightDist)
		{
			if (bottomDist < topDist)
				{
					if (leftDist < bottomDist)
					{
						angle = new Direction((float)Math.PI);
					}
					else
						{
	
							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (leftDist < topDist)
			{
				angle = new Direction((float)Math.PI);
			}
			else
				{
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		else
		{
			if (bottomDist < topDist)
				{
					if (rightDist < bottomDist)
					{
						angle = new Direction(0);
					}
					else
						{
							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (rightDist < topDist)
			{
				angle = new Direction(0);
			}
			else
				{
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		
		Debug.debug_print("building at angle " + angle.getAngleDegrees());
		
		int turnCount = 0;
		while (!rc.canHireGardener(angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			if (rc.canHireGardener(angle))
			{
				Debug.debug_print("built gardener: " + angle.getAngleDegrees());
				Debug.debug_print("(" + rc.getLocation().x + ", " + rc.getLocation().y + ")");
				Globals.updateUnitCounts(UnitType.GARDENER);
				rc.hireGardener(angle);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
