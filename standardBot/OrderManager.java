package standardBot;

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
			//if (current.location.distanceTo(rc.getLocation()) <= 4) {
			//	if (rc.senseNearbyRobots(-1, enemy).length == 0) {
			//		current.TTL = 0;
					
			//	}
			//}
			if (UnitType.isCombat() && current.type == 0){
				currentOrder = current;
				return;
			}
		}
		currentOrder = null;
	}
	
	public static MapLocation getTarget() { 
		return currentOrder.location;
	}
	
	public static boolean shouldMove() {
		return hasOrder() && currentOrder.type == 0;
	}
	
}
