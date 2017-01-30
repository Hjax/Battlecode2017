package standardBot;

import battlecode.common.*;

public class BuildManager extends Bot{
	public static int UNDECIDED = 0;
	public static int STANDARD = 1;
	public static int AGGRESSIVE = 2;
	private static int treeCount = 0;
	private static float density = 0;
	public static int treesPlanted = 0;

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
	
	public static void decideBuild() throws Exception {
		density = getDensity();
     	if (enemyArchons.length == 1 && rc.getLocation().distanceTo(enemyArchons[0]) < 32 && density < 0.5)
     	{
     		Globals.setStrat(AGGRESSIVE);
     	} else {
     		Globals.setStrat(STANDARD);
     	}
	}
	
	public static void executeBuild() throws Exception {
		treeCount = rc.getTreeCount();
		density = getDensity();
		if (!buildNextUnit()) {
			if (UnitType.getType() == UnitType.TRAINER || UnitType.getType() == UnitType.ARCHON){
				return;
				}
			if (rc.getTeamBullets() >= 50 && rc.isBuildReady() && Gardener.roost != null) {
				Debug.debug_print("Trying to plant a tree");
				plantSpacedTree(Gardener.roost);
			}
		}
	}
	
	public static float scoreLumberjack() throws GameActionException {
		int lcount = Globals.getUnitCount(UnitType.LUMBERJACK);
		if (Globals.getLastUnit() == UnitType.LUMBERJACK && (rc.getRoundNum() - Globals.getLastUnitRound()) < 21) {
			 lcount++;
		}
		return Math.min((rc.senseNearbyTrees(7, Team.NEUTRAL).length / 40.0f), 1.0f) * 0.40f + density * 0.5f + (20.0f - lcount) / 20.0f * 0.20f;
	}
	
	public static float scoreSoldier() throws GameActionException {
		int scount = Globals.getUnitCount(UnitType.SOLDIER);
		if (Globals.getLastUnit() == UnitType.SOLDIER && (rc.getRoundNum() - Globals.getLastUnitRound()) < 21) {
			 scount++;
		}
		boolean needsSoldiers = false;
		for (int i = 0; i < enemiesMaxRange.length; i++) {
			if ((enemiesMaxRange[i].type != RobotType.ARCHON && enemiesMaxRange[i].type != RobotType.SCOUT) || (enemiesMaxRange[i].location.distanceTo(rc.getLocation()) <= 3 && enemiesMaxRange[i].type == RobotType.SCOUT)) {
				needsSoldiers = true;
				break;
			}
		}
		if (scount == 0 || needsSoldiers) {
			return 1.0f;
		}
		if (Globals.getStrat() == AGGRESSIVE) {
			return Math.min(((float) treeCount + 4) / scount, 1.0f);
		} else {
			return Math.min(((float) (treeCount + 2) / 2.0f) / scount, 1.0f);
		}
	}
	
	public static float scoreTank() throws GameActionException {
		return (Math.min(treeCount / 60.0f, 1.0f));
	}
	
	public static float scoreScout() throws GameActionException {
		int scount = Globals.getUnitCount(UnitType.SCOUT);
		if (Globals.getLastUnit() == UnitType.SCOUT && (rc.getRoundNum() - Globals.getLastUnitRound()) < 21) {
			 scount++;
		}
		return ((scount == 0) ? 1:0) * 0.2f + ((Globals.getOrderCount() == 0 && scount < 6) ? 1:0) * 0.8f;
	}
	
	public static float scoreGardener() throws GameActionException {
		int gcount = Globals.getUnitCount(UnitType.GARDENER);
		Debug.debug_print("gardener count: " + gcount);
		Debug.debug_print("stuck gardener count: " + Globals.getStuckGardeners());
		if (Globals.getUnitCount(UnitType.ARCHON) == 0)
		{
			return 0;
		}
		if (Globals.getLastUnit() == UnitType.GARDENER && (rc.getRoundNum() - Globals.getLastUnitRound()) < 21) {
			 gcount++;
		}
		if (gcount > 14) {
			return 0.0f;
		}
		if (gcount - Globals.getStuckGardeners() <= 0 && (Globals.getLastUnit() != UnitType.GARDENER || (rc.getRoundNum() - Globals.getLastUnitRound()) > 21)) {
			return 1.0f;
		}
		return (Math.min(((float) treeCount) / ((gcount  - Globals.getStuckGardeners()/2) * 5.0f), 1.0f));
	}
	
	
	public static boolean buildNextUnit() throws Exception { 
		
		float soldier_score = scoreSoldier();
		float tank_score = scoreTank();
		float scout_score = scoreScout();
		float lumberjack_score = scoreLumberjack();
		float gardener_score = scoreGardener();
		
		Debug.debug_print("Soldier score: " + Float.toString(soldier_score));
		Debug.debug_print("Tank score: " + Float.toString(tank_score));
		Debug.debug_print("Scout score: " + Float.toString(scout_score));
		Debug.debug_print("Lumberjack score: " + Float.toString(lumberjack_score));
		Debug.debug_print("Gardener score: " + Float.toString(gardener_score));
		
		float max_score = Math.max(Math.max(Math.max(soldier_score, tank_score), scout_score), lumberjack_score);

		Debug.debug_print("Trying to build a unit");
		
		if (rc.isBuildReady() && treesPlanted < 8) {
			Debug.debug_print("Ready to build");
			
			if (rc.getType() == RobotType.ARCHON) 
			{
				RobotInfo[] allies = rc.senseNearbyRobots(6, ally);
				int countAlly = 0;
				for (countAlly = 0; countAlly < allies.length; countAlly++)
				{
					if (allies[countAlly].getType() == RobotType.GARDENER)
					{
						break;
					}
				}
				if (countAlly >= allies.length && rc.getTeamBullets() > 105 && gardener_score == 1)
				{
					Debug.debug_print("Trying to build gardener");
					trainGardener();
					return true;
				}
				return false;
			}
			
			if (Math.abs(Math.max(max_score, soldier_score) - soldier_score) < 0.001f) {
				System.out.println("I want to build a soldier");
				if (rc.getTeamBullets() > RobotType.SOLDIER.bulletCost + 5){
					Debug.debug_print("Trying to build SOLDIER");
					trainUnit(RobotType.SOLDIER, Gardener.roost);
					return true;
				}
			}
			else if (Math.abs(Math.max(max_score, scout_score) - scout_score) < 0.001f) {
				if (rc.getTeamBullets() > RobotType.SCOUT.bulletCost){
					Debug.debug_print("Trying to build scout");
					trainUnit(RobotType.SCOUT, Gardener.roost);
					return true;
				}
			}
			else if (Math.abs(Math.max(max_score, lumberjack_score) - lumberjack_score) < 0.001f) {
				if (rc.getTeamBullets() > RobotType.LUMBERJACK.bulletCost){
					Debug.debug_print("Trying to build lumberjack");
					trainUnit(RobotType.LUMBERJACK, Gardener.roost);
					return true;
				}
			}

			else if (Math.abs(Math.max(max_score, tank_score) - tank_score) < 0.001f) {
				if (rc.getTeamBullets() > RobotType.TANK.bulletCost){
					Debug.debug_print("Trying to build tank");
					trainUnit(RobotType.TANK, Gardener.roost);
					return true;
				}
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

	public static void trainUnit(RobotType unit, MapLocation roost) throws Exception
	{
		if (roost == null)
		{
			roost = rc.getLocation();
		}
		Debug.debug_print("training unit");
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
		float spacing = 2.1f;
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(testAngle, spacing), 1.05f) || !rc.onTheMap(roost.add(testAngle, spacing), 1.05f)) && turnCount++ < 60)
		{
			rc.setIndicatorDot(roost.add(testAngle, spacing), 155, 155, 155);
			
			testAngle = angle.rotateRightDegrees(3 * turnCount);
			if ((rc.isCircleOccupiedExceptByThisRobot(roost.add(testAngle, spacing), 1.05f) || !rc.onTheMap(roost.add(testAngle, spacing), 1.05f)))
			{
				rc.setIndicatorDot(roost.add(testAngle, spacing), 155, 155, 155);
				testAngle = angle.rotateLeftDegrees(3 * turnCount);
			}
		}
		if (unit == RobotType.TANK)
		{
			Utilities.moveTo(rc.getLocation().add(testAngle, spacing - 1));
		}

		try {
			if (rc.canBuildRobot(unit,  testAngle))
			{
				Globals.updateUnitCounts(UnitType.getType(unit));
				Globals.setLastUnit(UnitType.getType(unit));
				Globals.setLastUnitRound(rc.getRoundNum());
				rc.buildRobot(unit, testAngle);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	public static float getDensity() throws GameActionException{
		Debug.debug_bytecode_start();
		float total = 0;
		TreeInfo blockingTree = null;
		for (float i = 0; i < 2 * Math.PI; i += Math.PI / 3) {
			MapLocation clone = rc.getLocation().add(i, rc.getType().bodyRadius - 0.01f);
			for (int j = 1; j <= 15; j++) 
			{
				clone = clone.add(i, (rc.getType().sensorRadius - rc.getType().bodyRadius) / 15);
				blockingTree = rc.senseTreeAtLocation(clone);
				if (!rc.onTheMap(clone) || (blockingTree != null && blockingTree.getTeam() != rc.getTeam())) 
				{ 
					if (!rc.onTheMap(clone))
					{
						total += (16 - j)/360f;
					}
					else
					{
						total += (16 - j)/90f;
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
		float spacing = 2.1f;
		Debug.debug_print("trying to plant");
		roost = rc.getLocation();
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(angle, spacing), 1.05f) || !rc.onTheMap(roost.add(angle, spacing), 1.05f)) && turnCount++ < 6)
		{
			rc.setIndicatorDot(roost.add(angle, spacing), 155, 155, 155);
			
			angle = angle.rotateLeftDegrees(60);
		}
		rc.setIndicatorDot(roost.add(angle, spacing), 0, 255, 0);
		angle = angle.rotateLeftDegrees(60);
		turnCount++;
		while ((rc.isCircleOccupiedExceptByThisRobot(roost.add(angle, spacing), 1.05f) || (Globals.getUnitCount(UnitType.GARDENER) < 2 && (rc.senseNearbyTrees(roost.add(angle, spacing), 1.05f, ally).length + rc.senseNearbyTrees(roost.add(angle, spacing), 2.5f, Team.NEUTRAL).length > 0 || !rc.onTheMap(roost.add(angle, spacing), 2.5f))) || !rc.onTheMap(roost.add(angle, spacing), 1.05f)) && turnCount++ < 6)
		{
			rc.setIndicatorDot(roost.add(angle, spacing), 255, 255, 255);
			
			angle = angle.rotateLeftDegrees(60);
		}
		try 
		{
			if (turnCount < 6)
			{
				if (!rc.hasMoved())
				{
					Debug.debug_print("moving to plant");
					Utilities.moveTo((roost.add(angle, spacing - 2)));
				}
				else
				{
					Debug.debug_print("already moved, can't plant");
				}
				if (rc.getLocation().distanceTo(roost.add(angle, spacing)) <= 2.05f)
				{
					Gardener.isStuck = false;
					Gardener.buildIndex++;
					Debug.debug_print("Planting");
					rc.plantTree(rc.getLocation().directionTo(roost.add(angle, spacing)));
					treesPlanted++;
				}
				else
				{
					Gardener.isStuck = true;
					return false;
				}
				
				return true;
			}
			else
			{
				Gardener.isStuck = true;
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
				Globals.setLastUnit(UnitType.GARDENER);
				Globals.setLastUnitRound(rc.getRoundNum());
				rc.hireGardener(angle);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
