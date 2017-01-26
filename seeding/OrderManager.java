package seeding;

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
		for (int i = 0; i < num_orders; i++){
			long current = Memory.getOrder(i);
			if (checkDelete(current, i)){
				if (UnitType.isCombat() && Order.getType(current) == 0){
					currentOrder = new Order(current, i);
					return;
				}
			}
		}
		currentOrder = null;
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
	
	// optimized 1/25/2017
	public static boolean hasCloseOrder() throws GameActionException {
		int order_count = Globals.getOrderCount();
		for (int i = 0; i < order_count; i++){
			if (rc.getLocation().distanceTo(Order.getLocation(Memory.getOrder(i))) <= 10){
				return true;
			}
		}
		return false;
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
		if (enemiesMaxRange.length == 0){
			return;
		}
		int order_count = Globals.getOrderCount();
		for (int i = 0; i < order_count; i++){
			if (rc.getLocation().distanceTo(Order.getLocation(Memory.getOrder(i))) <= 15) {
				return;
			}
		}
		for (int i = 0; i < enemiesMaxRange.length; i++) {
			if (enemiesMaxRange[i].getType() != RobotType.SCOUT) {
				Memory.addOrder(new Order(0, enemiesMaxRange[i].location, 3000, -1));
				return;
			}
		}
	}
	
	public static boolean shouldMove() {
		return hasOrder() && currentOrder.type == 0;
	}
	
}
