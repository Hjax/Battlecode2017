package first_bot;

import battlecode.common.*;

public class Archon extends Bot{
	public static void Start(RobotController RobCon){
		Bot.Init(RobCon);


		try {
			debug_memory();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("We crashed");
		}

    	
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {


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
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	public static void debug_memory_counts() throws GameActionException{
		System.out.println("Running Memory Debug");
		System.out.println("Number of Orders: " + Memory.getNumOrders());
		System.out.println("Number of Allies: " + Memory.getNumAllies());
		System.out.println("Number of Enemies: " + Memory.getNumEnemies());
		Memory.setNumOrders(1);
		Memory.setNumAllies(2);
		Memory.setNumEnemies(3);
		Memory.commit();
		System.out.println("Number of Orders: " + Memory.getNumOrders());
		System.out.println("Number of Allies: " + Memory.getNumAllies());
		System.out.println("Number of Enemies: " + Memory.getNumEnemies());
	}
	public static void debug_memory_storage() {
		
	}
}
