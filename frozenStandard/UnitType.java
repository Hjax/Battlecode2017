package frozenStandard;

import battlecode.common.RobotType;

public class UnitType extends Bot {
	public static final int TRAINER 	= 0;
	public static final int ARCHON 		= 1;
	public static final int GARDENER 	= 2;
	public static final int SCOUT		= 3;
	public static final int LUMBERJACK 	= 4;
	public static final int TANK 		= 5;
	public static final int SOLDIER 	= 6;
	
	public static int getType(RobotType r) throws Exception{
		switch (r) {
	        case ARCHON:
	        	return ARCHON;
	        case GARDENER:
	        	return GARDENER;
	        case SOLDIER:
	        	return SOLDIER;
	        case LUMBERJACK:
	        	return LUMBERJACK;
	        case TANK:
	        	return TANK;
	        case SCOUT:
	        	return SCOUT;
	        default:
	        	throw new Exception("Unexpected Robot Type: " + rc.getType()); 	
	    }
	}

	
	public static int getType() throws Exception{
		return getType(rc.getType());
	}
	public static boolean isCombat() throws Exception {
		return getType() >= 5;
	}
}
