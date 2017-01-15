package standardBot;

public class UnitType extends Bot {
	public static int TRAINER 		= 0;
	public static int SOLDIER 		= 1;
	public static int TANK 			= 2;
	public static int SCOUT			= 3;
	public static int LUMBERJACK 	= 4;
	public static int GARDENER 		= 5;
	public static int ARCHON 		= 6;
	
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
