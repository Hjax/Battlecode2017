package standardBot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws GameActionException{
		
        Debug.debug_print("Starting Archon Code");
        boolean tryBuild = false;
     	
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
     	
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try 
            {
            	startTurn();
            	
            	Debug.debug_bytecode_start();
            	OrderManager.checkCreateOrderCheap();
            	Debug.debug_bytecode_end("create orders");
            	
            	TreeInfo trees[] = rc.senseNearbyTrees();
            	if (trees.length > 0){
            		for (TreeInfo tree: trees){
            			if (tree.getContainedBullets() >= 10){
            				Utilities.moveTo(tree.location);
            				break;
            			}
            		}
            	}
            	
                // dodge
            	if (!rc.hasMoved())
            	{
            		Utilities.tryMove(neo());
            	}
            	

                // build gardeners at reasonable times
                int round = rc.getRoundNum();
                if ((Globals.getUnitCount(UnitType.GARDENER) <= 1 || rc.getTreeCount() / Globals.getUnitCount(UnitType.GARDENER) >= 2) &&(Globals.getUnitCount(UnitType.GARDENER) < 14 || Globals.getUnitCount(UnitType.TRAINER) < 4) && ((round == 1 && isFirst) || (Globals.getUnitCount(UnitType.GARDENER) == 0 && rc.getRoundNum() > 30) || (Globals.getUnitCount(UnitType.GARDENER) - 2 < (Globals.getUnitCount(UnitType.SOLDIER) + 3 * Globals.getUnitCount(UnitType.TANK) + Globals.getUnitCount(UnitType.SCOUT) + Globals.getUnitCount(UnitType.LUMBERJACK))/6 && rc.getRoundNum() > 100) || Globals.getUnitCount(UnitType.TRAINER) < Math.floor((Globals.getUnitCount(UnitType.GARDENER))/2)) || rc.getTeamBullets() > 600)
                	{tryBuild = true;}
             
                if (tryBuild && rc.getTeamBullets() > 120) 
                {
                	trainGardener();
                    tryBuild = false;
                }
                



            } catch (Exception e) {
            	Debug.debug_print("Exception in Archon mainloop");
            	e.printStackTrace();
            	}
            endTurn();
        }
	}
	
	private static void trainGardener() throws GameActionException
	{
		Direction angle = new Direction(0);
		float leftDist, rightDist, topDist, bottomDist;
		leftDist = 1000;
		rightDist = 1000;
		topDist = 1000;
		bottomDist = 1000;
		
		
		
		if (Globals.getLeftEdge() != -1)
		{
			leftDist = rc.getLocation().x - Globals.getLeftEdge();
		}
		if (Globals.getRightEdge() != -1)
		{
			rightDist = Globals.getRightEdge() - rc.getLocation().x;
		}
		if (Globals.getBottomEdge() != -1)
		{
			bottomDist =  rc.getLocation().y - Globals.getBottomEdge();
		}
		if (Globals.getTopEdge() != -1)
		{
			topDist = Globals.getTopEdge() - rc.getLocation().y;
		}
		
		
		if (leftDist < rightDist)
		{
			if (bottomDist < topDist)
				{
					if (leftDist < bottomDist)
					{
						angle = new Direction((float)Math.PI);
					}
					else
						{

							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (leftDist < topDist)
			{
				angle = new Direction((float)Math.PI);
			}
			else
				{
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		else
		{
			if (bottomDist < topDist)
				{
					if (rightDist < bottomDist)
					{
						angle = new Direction(0);
					}
					else
						{
							angle = new Direction((float)Math.PI * 3 / 2);
						}
				}
			else if (rightDist < topDist)
			{
				angle = new Direction(0);
			}
			else
				{
					angle = new Direction((float)Math.PI * 1 / 2);
				}
		}
		
		Debug.debug_print("building at angle " + angle.getAngleDegrees());
		
		int turnCount = 0;
		while (!rc.canHireGardener(angle) && turnCount++ < 90)
		{
			angle = angle.rotateRightDegrees(4);
		}
		try {
			if (rc.canHireGardener(angle))
			{
				Debug.debug_print("built gardener: " + angle.getAngleDegrees());
				Debug.debug_print("(" + rc.getLocation().x + ", " + rc.getLocation().y + ")");
				rc.hireGardener(angle);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

}
