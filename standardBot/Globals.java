package standardBot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import first_bot.Utilities;

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
		return rc.readBroadcast(0);
	}
	
	public static float getBottomEdge() throws GameActionException {
		return rc.readBroadcast(1);
	}
	
	public static float getRightEdge() throws GameActionException {
		return rc.readBroadcast(2);
	}
	
	public static float getLeftEdge() throws GameActionException {
		return rc.readBroadcast(3);
	}
	
	public static int getArchonCount() throws GameActionException{
		return rc.readBroadcast(4);
	}
	
	public static int getGardenerCount() throws GameActionException{
		return rc.readBroadcast(5);
	}
	
	public static int getTrainerCount() throws GameActionException{
		return rc.readBroadcast(6);
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
	
	
}
