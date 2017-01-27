package standardBot;

import battlecode.common.*;

public strictfp class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     * @throws Exception 
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws Exception {
		Bot.Init(rc);

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                Archon.Start(rc);
                break;
            case GARDENER:
            	if (Bot.behaviorType == 0)
            		{Gardener.Start(rc);}
            	else 
            		{Trainer.Start(rc);}
                break;
            case SOLDIER:
                Soldier.Start(rc);
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
