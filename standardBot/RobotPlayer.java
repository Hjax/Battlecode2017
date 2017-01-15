package standardBot;

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
            		if (nearby[bot].type == RobotType.ARCHON && Math.abs(new Direction(nearby[bot].getLocation(), rc.getLocation()).getAngleDegrees() % 4) >= 1.25f && Math.abs(new Direction(nearby[bot].getLocation(), rc.getLocation()).getAngleDegrees() % 4) <= 2.75)
            		{
            			garden = false;
            			
            		}
            		System.out.println(new Direction(nearby[bot].getLocation(), rc.getLocation()).getAngleDegrees() % 4);
        			System.out.println("(" + nearby[bot].getLocation().x + ", " + nearby[bot].getLocation().y + ")");
            	}
            	if (garden)
            		{Gardener.Start(rc);}
            	else 
            		{Trainer.Start(rc);}
                break;
            case SOLDIER:
                Guard.Start(rc);
                break;
            case LUMBERJACK:
                LumberJack.Start(rc);
                break;
            case TANK:
            	Soldier.Start(rc);
            	break;
            case SCOUT:
            	Scout.Start(rc);
            	break;
            default:
            	throw new Exception("Unknown Robot Type: " + rc.getType());
            	
        }
	}
}
