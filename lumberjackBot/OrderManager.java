package lumberjackBot;

import battlecode.common.*;

public class OrderManager extends Bot{

	private static Order currentOrder;
	
	public static boolean hasOrder(){
		return currentOrder != null;
	}
	
	// returns the first order that can be executed by this robot
	// will need to be updated to deal with more order types in the future
	public static void updateOrders() throws Exception {

		for (int i = 0; i < Globals.getOrderCount(); i++){
			Order current = Memory.getOrder(i);
			if (checkDelete(current)){
				if (UnitType.isCombat() && current.type == 0){
					currentOrder = current;
					return;
				}
			}
		}
		currentOrder = null;
	}
	
	public static boolean checkDelete(Order o) throws GameActionException{
		System.out.println("running check delete");
		if (o.location.distanceTo(rc.getLocation()) <= 4) {
			System.out.println(1);
			if (rc.senseNearbyRobots(-1, enemy).length == 0) {
				System.out.println(2);
				Memory.deleteOrder(o);
				return false;
			}
		}
		return true;
	}
	
	public static MapLocation getTarget() { 
		return currentOrder.location;
	}
	
	public static boolean hasCloseOrder() throws GameActionException {
		for (int i = 0; i < Globals.getOrderCount(); i++){
			if (rc.getLocation().distanceTo(Memory.getOrder(i).location) <= 10){
				return true;
			}
		}
		return false;
	}
	
	public static void checkCreateOrder() throws Exception {
robot:	for (RobotInfo enemy: rc.senseNearbyRobots(-1, enemy)){
			for (int i = 0; i < Globals.getOrderCount(); i++){
				if (enemy.location.distanceTo(Memory.getOrder(i).location) <= 10) {
					continue robot;
				}
			}
			Memory.addOrder(new Order(0, enemy.location, 3000, -1));
			break;
		}
	}
	public static void checkCreateOrderCheap() throws Exception {
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, enemy);
		if (enemies.length == 0){
			return;
		}
		for (int i = 0; i < Globals.getOrderCount(); i++){
			if (rc.getLocation().distanceTo(Memory.getOrder(i).location) <= 15) {
				return;
			}
		}
		for (RobotInfo enemy: enemies) {
			if (enemy.getType() != RobotType.SCOUT) {
				Memory.addOrder(new Order(0, enemies[0].location, 3000, -1));
				return;
			}
		}
	}
	
	public static boolean shouldMove() {
		return hasOrder() && currentOrder.type == 0;
	}
	
}
