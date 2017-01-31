package standardBot;

import battlecode.common.*;

public class OrderManager extends Bot{

	private static Order currentOrder;
	
	public static boolean hasOrder(){
		return currentOrder != null;
	}
	
	// returns the first order that can be executed by this robot
	// will need to be updated to deal with more order types in the future
	// optimized 1/25/2017
	public static void updateOrders() throws Exception {
		int num_orders = Globals.getOrderCount();
		long best = 0;
		int best_index = -1;
		currentOrder = null;
		for (int i = 0; i < num_orders; i++){
			long current = Memory.getOrder(i);
			if (checkDelete(current, i)){
				if (((UnitType.isCombat() || rc.getType() == RobotType.ARCHON || rc.getType() == RobotType.LUMBERJACK) && (Order.getType(current) == 0 || (Order.getType(current) == 1 && rc.getLocation().distanceTo(Order.getLocation(current)) <= 15))) && 
					(best == 0 || (Order.getLocation(best).distanceTo(rc.getLocation()) > Order.getLocation(current).distanceTo(rc.getLocation())))) {
					best = current;
					best_index = i;
				}
			}
			if (best != 0) {
				currentOrder = new Order(best, best_index);
			}
			
		}
	}
	
	// optimized 1/25/2017
	public static boolean checkDelete(long o, int loc) throws GameActionException{
		if (Order.getLocation(o).distanceTo(rc.getLocation()) <= (rc.getType().sensorRadius - 4) ) {
			if (enemiesMaxRange.length == 0) {
				Memory.deleteOrder(new Order(o, loc));
				return false;
			}
		}
		return true;
	}
	
	public static MapLocation getTarget() { 
		return currentOrder.location;
	}
	
	public static void checkCreateOrder() throws Exception {
robot:	for (RobotInfo enemy: enemiesMaxRange){
			for (int i = 0; i < Globals.getOrderCount(); i++){
				if (enemy.location.distanceTo(Order.getLocation(Memory.getOrder(i))) <= 10) {
					continue robot;
				}
			}
			Memory.addOrder(new Order(0, enemy.location, 3000, -1));
			break;
		}
	}
	
	// optimized 1/25/2017
	public static void checkCreateOrderCheap() throws Exception {
		if (enemiesMaxRange.length == 0 || rc.getRoundNum()  < 3){
			return;
		}
		int order_count = Globals.getOrderCount();
		for (int i = 0; i < order_count; i++){
			if (rc.getLocation().distanceTo(Order.getLocation(Memory.getOrder(i))) <= 15) {
				return;
			}
		}
		for (int i = 0; i < enemiesMaxRange.length; i++) {
			if (enemiesMaxRange[i].getType() != RobotType.SCOUT && (enemiesMaxRange[i].getType() != RobotType.ARCHON || (rc.getRoundNum() > 500 || !isStuckArchon(enemiesMaxRange[i].location)))) {
				Memory.addOrder(new Order(0, enemiesMaxRange[i].location, 3000, -1));
				return;
			}
		}
	}
	
	public static boolean isStuckArchon(MapLocation loc) throws GameActionException {
		for (int i = 0; i < enemyArchons.length; i++){
			if (loc.distanceTo(enemyArchons[i]) < 3) {
				if ((Globals.getArchonBits() & (int) Math.pow(2, i)) != 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean shouldMove() {
		return hasOrder() && (currentOrder.type == 0 || currentOrder.type == 1);
	}
	
	public static boolean shouldGroundFire() {
		return hasOrder() && (currentOrder.type == 1);
	}
	
}
