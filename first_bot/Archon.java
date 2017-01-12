package first_bot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon){
		Bot.Init(RobCon);
  	
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	startTurn();
    			debug_memory_counts();

            	
                // Generate a random direction
                Direction dir = Utilities.randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && Math.random() < .01) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                Utilities.tryMove(Utilities.randomDirection());

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                
            	System.out.println(rc.readBroadcast(0));
            	
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	public static void debug_memory_counts() throws GameActionException{
		System.out.println("Running Memory Debug");
		//System.out.println("Number of Orders: " + Memory.getNumOrders());
		System.out.println("Number of Allies: " + Memory.getNumAllies());
		//System.out.println("Number of Enemies: " + Memory.getNumEnemies());
	}
	public static void debug_memory_storage() throws GameActionException {
		System.out.println("Running Memory Storage Test");
		AllyData foo = new AllyData(0, new MapLocation(5, 5), 30, true);
		Memory.write(20, foo.toInt());
		AllyData bar = new AllyData(Memory.read(20));
		System.out.println(bar.type);
		System.out.println(bar.location.x);
		System.out.println(bar.location.y);
		System.out.println(bar.hp);
		System.out.println(bar.alive);
	}
}
