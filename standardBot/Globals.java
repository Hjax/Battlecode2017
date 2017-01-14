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
	
	
}
