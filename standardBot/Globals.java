package standardBot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class Globals extends Bot{
	public static void initEdges() throws Exception {
		setTopEdge(-1);
		setBottomEdge(-1);
		setLeftEdge(-1);
		setRightEdge(-1);
	}
	
	public static void updateEdges() throws Exception {
		if (getTopEdge() == -1){
			setTopEdge(Utilities.edgeInDirection(Direction.getNorth()));
		}
		if (getBottomEdge() == -1){
			setBottomEdge(Utilities.edgeInDirection(Direction.getSouth()));
		}
		if (getRightEdge() == -1){
			setRightEdge(Utilities.edgeInDirection(Direction.getEast()));
		}
		if (getLeftEdge() == -1){
			setLeftEdge(Utilities.edgeInDirection(Direction.getWest()));
		}
	}
	
	public static void clearUnitCounts() throws Exception {
		for (int i = 0; i <= 6; i++){
			int location = 4 + (2 * i) + (rc.getRoundNum() % 2);
			Memory.writeGlobal(location, 0);
		}
	}
	
	public static void updateUnitCounts() throws Exception {
		int location = 4 + (2 * UnitType.getType()) + (rc.getRoundNum() % 2);
		Memory.writeGlobal(location, Memory.readGlobal(location) + 1);
	}
	
	public static int getUnitCount(int type) throws GameActionException {
		return Memory.readGlobal(4 + (2 * type) + (1 - rc.getRoundNum() % 2));
	}
	
	public static float getTopEdge() throws GameActionException {
		return Memory.readGlobal(0);
	}
	
	public static float getBottomEdge() throws GameActionException {
		return Memory.readGlobal(1);
	}
	
	public static float getRightEdge() throws GameActionException {
		return Memory.readGlobal(2);
	}
	
	public static float getLeftEdge() throws GameActionException {
		return Memory.readGlobal(3);
	}
	
	public static void setTopEdge(float y) throws Exception {
		Memory.writeGlobal(0, Math.round(y));
	}
	
	public static void setBottomEdge(float y) throws Exception {
		Memory.writeGlobal(1, Math.round(y));
	}
	
	public static void setRightEdge(float x) throws Exception {
		Memory.writeGlobal(2, Math.round(x));
	}
	
	public static void setLeftEdge(float x) throws Exception {
		Memory.writeGlobal(3, Math.round(x));
	}
	
	public static int getOrderCount() throws GameActionException{
		return Memory.readGlobal(18);
	}
	
	public static void setOrderCount(int n) throws Exception {
		Memory.writeGlobal(18, n);
	}
	
	public static int getRoundNumber() throws GameActionException {
		return Memory.readGlobal(19);
	}
	
	public static void setRoundNumber(int n) throws Exception {
		Memory.writeGlobal(19, n);
	}
	
	public static int getStrat() throws GameActionException {
		return Memory.readGlobal(20);
	}
	
	public static void setStrat(int n) throws Exception {
		Memory.writeGlobal(20, n);
	}
}
