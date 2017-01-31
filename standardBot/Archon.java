package standardBot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon) throws Exception{
		
        Debug.debug_print("Starting Archon Code");
        
        int[] symmetries = Utilities.symmetrizeStarts(allyArchons, enemyArchons);
     	
        if (BuildManager.isStuck()) {
        	int i;
        	for (i = 0; i < allyArchons.length; i++){
        		if (allyArchons[i].equals(rc.getLocation())) {
        			break;
        		}
        	}
        	Globals.setArchonBits(Globals.getArchonBits() | (int) Math.pow(2, symmetries[i]));
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
