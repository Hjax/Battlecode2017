package ecoBot;

import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     * @throws Exception 
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws Exception {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                Archon.Start(rc);
                break;
            case GARDENER:
            	boolean garden = true;
            	RobotInfo nearby[] = rc.senseNearbyRobots(2.0f);
            	for (int bot = 0; bot < nearby.length; bot++)
            	{
            		if (nearby[bot].type == RobotType.ARCHON && new Direction(nearby[bot].getLocation(), rc.getLocation()).getAngleDegrees() % 4 >= 1.5f && new Direction(nearby[bot].getLocation(), rc.getLocation()).getAngleDegrees() % 4 <= 3)
            			{garden = false;}
            	}
            	if (garden)
            		{Gardener.Start(rc);}
            	else 
            		{Gardener.Start(rc);}
                break;
            case SOLDIER:
                Soldier.Start(rc);
                break;
            case LUMBERJACK:
                LumberJack.Start(rc);
                break;
            case TANK:
            	Tank.Start(rc);
            	break;
            default:
            	throw new Exception("Unknown Robot Type: " + rc.getType());
            	
        }
	}
}
