package gardenBot;

public class UnitType extends Bot {
	public static final int TRAINER 	= 0;
	public static final int SOLDIER 	= 1;
	public static final int TANK 		= 2;
	public static final int SCOUT		= 3;
	public static final int LUMBERJACK 	= 4;
	public static final int GARDENER 	= 5;
	public static final int ARCHON 		= 6;
	
	public static int getType() throws Exception{
		switch (rc.getType()) {
	        case ARCHON:
	        	return ARCHON;
	        case GARDENER:
	        	if (behaviorType == 0){
	        		return GARDENER;
	        	}
	        	return TRAINER;
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
}
