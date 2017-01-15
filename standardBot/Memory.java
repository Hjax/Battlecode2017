package standardBot;

import battlecode.common.*;

public class Memory extends Bot{
	// memory TODO
	// update allydata and friends to avoid the sign bit
	
	
	private static int min_global = 0;
	private static int max_global = 24;
	private static int min_address = 25;
	private static int max_address = 40;
	private static int min_ally = 41;
	private static int max_ally = 536;
	private static int min_order = 537;
	private static int max_order = 636;
	private static int min_map_data = 637;
	private static int max_map_data = 999;
	
	public static int readGlobal(int index) throws GameActionException {
		if (index % 2 == 0){
			return Utilities.bitInterval(rc.readBroadcast(index / 2), 0, 14);
		} else {
			return Utilities.bitInterval(rc.readBroadcast((int) Math.floor(index / 2.0)), 15, 30);
		}
	}
	
	public static void writeGlobal(int index, int value) throws Exception {
		if (value > 32767) {
			throw new Exception("Value too large to write global");
		}
		if (index % 2 == 0) {
			rc.broadcast(index / 2, value + 32768 * readGlobal(index + 1));
		} else {
			rc.broadcast((int) Math.floor(index / 2.0), readGlobal(index - 1) + 32768 * value);
		}
	}
	
	public static int first_free_ally() throws Exception {
		int index = 0;
		for (int i = min_address; i <= max_address; i++){
			int current_cell = rc.readBroadcast(i);
			if (current_cell == 2147483647){
				index += 31;
				continue;
			}
			int value = 1;
			for (int j = 0; j < 31; j++){
				if ((current_cell & value) == 0){
					return index;
				}
				index++;
				value *= 2;
			}
		}
		throw new Exception("Out of Memory");
	}
	
	public static AllyData readAlly(int index) throws GameActionException {
		return new AllyData(rc.readBroadcast(index + min_ally));
	}
	
	public static void reserveAllyIndex(int index) throws Exception {
		if (index > 495) {
			throw new Exception("Out of Memory");
		}
		int cell = (int) Math.floor(index / 31) + min_address;
		int bit = index % 31;
		rc.broadcast(cell, rc.readBroadcast(cell) | (int) (Math.pow(2, bit)));
	}
	
	public static void freeAllyMemory(int index) throws GameActionException {
		int cell = (int) Math.floor(index / 31) + min_address;
		int bit = index % 31;
		rc.broadcast(cell, Math.min(rc.readBroadcast(cell) ^ (int) (Math.pow(2, bit)), rc.readBroadcast(cell)));
	}
	
	public static void writeAllyData(int index, int value) throws GameActionException {
		rc.broadcast(min_ally + index, value);
	}
	
	public static void pruneAllyMemory() throws GameActionException{
		for (int i = min_ally; i <= max_ally; i++){
			int current_int = rc.readBroadcast(i);
			if (current_int != 0){
				AllyData current = new AllyData(current_int);
				// if the alive variable is correct for the current turn
				// then it wasnt updated last turn
				// NOTE freeing memory does not write zeroes to the old location
				if (current.alive == ((rc.getRoundNum() % 2) == 1)){
					freeAllyMemory(i - min_ally);
				}
			}
		}
	}
	// TODO store orders in memory so we dont make the array too big
	public static void pruneOrders() throws Exception {
		int old_orders = Globals.getOrderCount();
		if (old_orders == 0){
			return;
		}
		int[] Orders = new int[old_orders];
		int order_count = 0;
		int total_orders = 0;
		for (int i = min_order; i <= max_order; i++){
			total_orders++;
			int current = rc.readBroadcast(i);
			if (current == 0){
				break;
			}
			Order current_order = new Order(current);
			
			// we need to reduce the time counter by one
			current_order.TTL -= 1;
			
			if (current_order.count <= 0 || current_order.TTL <= 0) {
				continue;
			}

			Orders[order_count++] = current_order.toInt();
		}
		for (int i = 0; i < total_orders; i++){
			if (i < order_count){
				rc.broadcast(i + min_order, Orders[i]);
			} else {
				rc.broadcast(i + min_order, 0);
			}
		}
		Globals.setOrderCount(order_count);
	}
	
	public static Order getOrder(int index) throws GameActionException{
		return new Order(rc.readBroadcast(min_order + index));
	}
	
	public static void addOrder(Order o) throws Exception{
		rc.broadcast(min_order + Globals.getOrderCount(), o.toInt());
		Globals.setOrderCount(Globals.getOrderCount() + 1);
	}
	
}
