package standardBot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class Globals extends Bot{
	public static void initEdges() throws Exception {
		System.out.println("Initializing edges");
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
	
	public static void incrementUnitCount(int type, int amount) throws Exception{
		switch (type){
			case UnitType.TRAINER:
				Globals.setTrainerCount(Globals.getTrainerCount() + amount);
				break;
			case UnitType.ARCHON:
				Globals.setArchonCount(Globals.getArchonCount() + amount);
				break;
			case UnitType.GARDENER:
				Globals.setGardenerCount(Globals.getGardenerCount() + amount);
				break;
			case UnitType.SOLDIER:
				Globals.setSoldierCount(Globals.getSoldierCount() + amount);
				break;
			case UnitType.LUMBERJACK:
				Globals.setLumberjackCount(Globals.getLumberjackCount() + amount);
				break;
			case UnitType.SCOUT:
				Globals.setScoutCount(Globals.getScoutCount() + amount);
				break;
			case UnitType.TANK:
				Globals.setTankCount(Globals.getTankCount() + amount);
				break;							
	}
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
	
	public static int getArchonCount() throws GameActionException{
		return Memory.readGlobal(4);
	}
	
	public static int getGardenerCount() throws GameActionException{
		return Memory.readGlobal(5);
	}
	
	public static int getTrainerCount() throws GameActionException{
		return Memory.readGlobal(6);
	}
	
	public static int getSoldierCount() throws GameActionException{
		return Memory.readGlobal(7);
	}
	
	public static int getLumberjackCount() throws GameActionException{
		return Memory.readGlobal(8);
	}
	
	public static int getScoutCount() throws GameActionException{
		return Memory.readGlobal(9);
	}
	
	public static int getTankCount() throws GameActionException{
		return Memory.readGlobal(10);
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
	
	public static void setArchonCount(int n) throws Exception{
		Memory.writeGlobal(4, n);
	}
	
	public static void setGardenerCount(int n) throws Exception{
		Memory.writeGlobal(5, n);
	}
	
	public static void setTrainerCount(int n) throws Exception{
		Memory.writeGlobal(6, n);
	}
	
	public static void setSoldierCount(int n) throws Exception{
		Memory.writeGlobal(7, n);
	}
	
	public static void setLumberjackCount(int n) throws Exception{
		Memory.writeGlobal(8, n);
	}
	
	public static void setScoutCount(int n) throws Exception{
		Memory.writeGlobal(9, n);
	}
	
	public static void setTankCount(int n) throws Exception{
		Memory.writeGlobal(10, n);
	}
	
	public static int getOrderCount() throws GameActionException{
		return Memory.readGlobal(11);
	}
	
	public static void setOrderCount(int n) throws Exception {
		Memory.writeGlobal(11, n);
	}
	
	public static int getRoundNumber() throws GameActionException {
		return Memory.readGlobal(12);
	}
	
	public static void setRoundNumber(int n) throws Exception {
		Memory.writeGlobal(12, n);
	}
	
	public static void setArchonScore(int m , int n) throws Exception {
		Memory.writeGlobal(13 + m, n);
	}
	
	public static int getArchonScore(int n) throws GameActionException{
		return Memory.readGlobal(13 + n);
	}
	
	public static int getStrat() throws GameActionException {
		return Memory.readGlobal(16);
	}
	
	public static void setStrat(int n) throws Exception {
		Memory.writeGlobal(16, n);
	}
	
	public static int getPreviousDefragger() throws GameActionException {
		return Memory.readGlobal(17);
	}
	
	public static void setPreviousDefragger(int n) throws Exception {
		Memory.writeGlobal(17, n);
	}
	

}
