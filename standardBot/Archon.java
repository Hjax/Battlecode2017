package standardBot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
        Debug.debug_print("Starting Archon Code");
        boolean tryBuild = false;
        try {
			Globals.setSuicide(0);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
     	
        if (BuildManager.isStuck()) {
        	Debug.debug_print("I am stuck!");
        }
        
        
     	try {
            BuildManager.decideBuild();
			Globals.initEdges();
			Globals.updateEdges();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
     	int archonNum = 0;
     	for (int i = 0; i < allyArchons.length; i++)
     	{
     		if (rc.getLocation().equals(allyArchons[i]))
     		{
     			archonNum = i;
     			break;
     		}
     	}
     	
     	float maxDist = 0;
		float myDist = 1000;
		for (int i = 0; i < enemyArchons.length; i++)
		{
			if (rc.getLocation().distanceTo(enemyArchons[i]) < myDist)
			{
				myDist = rc.getLocation().distanceTo(enemyArchons[i]);
			}
		}
		for (int i = 0; i < allyArchons.length; i++)
		{
			float minDist = 1000;
			for (int j = 0; j < enemyArchons.length; j++)
			{
				if (allyArchons[i].distanceTo(enemyArchons[j]) < minDist)
				{
					minDist = allyArchons[i].distanceTo(enemyArchons[j]);
				}
			}
			if (minDist > maxDist)
			{
				maxDist = minDist;
			}
		}
		
     	
     	int[] symmetries = Utilities.symmetrizeStarts(allyArchons, enemyArchons);
     	for (int i = 0; i < allyArchons.length; i ++)
     	{
     		if (i == 0)
     		{
     			rc.setIndicatorDot(allyArchons[i], 255, 0, 0);
     		}
     		if (i == 1)
     		{
     			rc.setIndicatorDot(allyArchons[i], 0, 255, 0);
     		}
     		if (i == 2)
     		{
     			rc.setIndicatorDot(allyArchons[i], 0, 0, 255);
     		}
     	}
     	for (int i = 0; i < enemyArchons.length; i ++)
     	{
     		if (symmetries[i] == 0)
     		{
     			rc.setIndicatorDot(enemyArchons[i], 255, 0, 0);
     		}
     		if (symmetries[i] == 1)
     		{
     			rc.setIndicatorDot(enemyArchons[i], 0, 255, 0);
     		}
     		if (symmetries[i] == 2)
     		{
     			rc.setIndicatorDot(enemyArchons[i], 0, 0, 255);
     		}
     	}
     	
     	
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
            	if (rc.getRoundNum() < 500 && rc.getRoundNum() - Globals.getLastUnitRound() > 300 && (Globals.getUnitCount(UnitType.ARCHON) > 1 || Globals.getUnitCount(UnitType.GARDENER) > 0) && Globals.getSuicide() == 0)
            	{
            		if (Math.abs(maxDist - myDist) < 0.01f)
            		{
            			Globals.setSuicide(1);
            			rc.disintegrate();
            		}
            		
            	}
            	
            	Debug.debug_bytecode_start();
            	OrderManager.checkCreateOrderCheap();
            	Debug.debug_bytecode_end("create orders");
            	
            	
                // dodge
            	if (!rc.hasMoved())
            	{
            		Utilities.tryMove(neo());
            	}
            	
            	BuildManager.executeBuild();
            	

            } catch (Exception e) {
            	Debug.debug_print("Exception in Archon mainloop");
            	e.printStackTrace();
            	}
            endTurn();
        }
	}
}
