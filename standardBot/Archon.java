package standardBot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws Exception{
		
        Debug.debug_print("Starting Archon Code");    
        int[] symmetries = Utilities.symmetrizeStarts(allyArchons, enemyArchons);
        
        try {
			Globals.setSuicide(0);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        int archonNum = 0;
     	for (archonNum = 0; archonNum < allyArchons.length; archonNum++)
     	{
     		if (rc.getLocation().equals(allyArchons[archonNum]))
     		{
     			break;
     		}
     	}
     	
        if (BuildManager.isStuck()) {
        	Globals.setArchonBits(Globals.getArchonBits() | (int) Math.pow(2, symmetries[archonNum]));
        	rc.setIndicatorDot(enemyArchons[symmetries[archonNum]], 255, 255, 255);
        	Debug.debug_print("I am stuck!");
        }
        
     	try {
            BuildManager.decideBuild();
		} catch (Exception e1) {
			e1.printStackTrace();
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
		
     	
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
            	if (rc.getRoundNum() < 500 && rc.getRoundNum() - Globals.getLastUnitRound() > 300 && (Globals.getUnitCount(UnitType.ARCHON) > 1 || Globals.getUnitCount(UnitType.GARDENER) > 0) && Globals.getSuicide() == 0 && rc.getTeamBullets() > 175 && rc.senseNearbyRobots(3, ally).length > 0)
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
            	
            	if (rc.getRoundNum() > 1 || Utilities.isFarArchon()) {
            		BuildManager.executeBuild();
            	}
            	
            	

            } catch (Exception e) {
            	Debug.debug_print("Exception in Archon mainloop");
            	e.printStackTrace();
            	}
            endTurn();
        }
	}
}
