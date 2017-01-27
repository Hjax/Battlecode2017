package standardBot;

import battlecode.common.*;

public class Trainer extends Bot{
	public static void Start(RobotController RobCon) throws Exception
	{
		
		Debug.debug_print("I'm a trainer!");
		
		behaviorType = 1;
		

        // The code you want your robot to perform every round should be in this loop
        while (true) 
        {
        	
        	startTurn();
        	
        	Debug.debug_bytecode_start();
        	OrderManager.checkCreateOrderCheap();
        	Debug.debug_bytecode_end("create order");
        	
        	rc.setIndicatorDot(rc.getLocation(), 0, 100, 100);
        	
			Utilities.tryMove(neo());

			if (Globals.getUnitCount(UnitType.SOLDIER) + 3 * Globals.getUnitCount(UnitType.TANK) + Globals.getUnitCount(UnitType.LUMBERJACK) + Globals.getUnitCount(UnitType.SCOUT) < 150)
			{
				// Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	        	try 
	        	{
	        		
	        		BuildManager.executeBuild();

	        	}

	        	catch (Exception e) {
	        		System.out.println("Gardener Exception");
	        		e.printStackTrace();
	        	}
			}
        	
        	endTurn();
        }
	}
}
