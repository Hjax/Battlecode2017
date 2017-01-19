package standardBot;

import battlecode.common.*;

public class Memory extends Bot{
	// memory TODO
	// update allydata and friends to avoid the sign bit
	
	private static int min_global = 0;
	private static int max_global = 49;
	private static int min_address = 50;
	private static int max_address = 65;
	private static int min_ally = 66;
	private static int max_ally = 577;
	private static int min_order = 578;
	private static int max_order = 677;
	private static int min_map_data = 678;
	private static int max_map_data = 999;
	private static long bits_zero = (long) Math.pow(2, 32);
	
	public static int readValue(int index) throws GameActionException {
		return rc.readBroadcast(index);
	}
	
	public static void writeValue(int index, int value) throws GameActionException{
		rc.broadcast(index, value);
	}
	
	public static long readBits(int index) throws GameActionException {
		return (rc.readBroadcast(index) + bits_zero);
	}
	
	public static void writeBits(int index, long value) throws GameActionException {
		rc.broadcast(index, (int) (value - bits_zero));
	}
	
	
	public static int readGlobal(int index) throws GameActionException {
		return readValue(min_global + index);
	}
	
	public static void writeGlobal(int index, int value) throws Exception {
		writeValue(min_global + index, value);
	}
	
	public static int first_free_ally() throws Exception {
		int index = 0;
		for (int i = min_address; i <= max_address; i++){
			long current_cell = readBits(i);
			if (current_cell == 6442450943l){
				index += 32;
				continue;
			}
			long value = 1;
			for (int j = 0; j < 32; j++){
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
		return new AllyData(readBits(index + min_ally));
	}
	
	public static void reserveAllyIndex(int index) throws Exception {
		if (index > 495) {
			throw new Exception("Out of Memory");
		}
		int cell = (int) Math.floor(index / 32) + min_address;
		int bit = index % 32;
		writeBits(cell, readBits(cell) | (int) (Math.pow(2, bit)));
	}
	
	public static void freeAllyMemory(int index) throws GameActionException {
		writeBits(min_ally + index, 0);
		int cell = (int) Math.floor(index / 32) + min_address;
		int bit = index % 32;
		writeBits(cell, Math.min(readBits(cell) ^ (int) (Math.pow(2, bit)), readBits(cell)));
	}
	
	public static void writeAllyData(int index, AllyData value) throws GameActionException {
		writeBits(min_ally + index, value.toInt());
	}
	
	public static int findAllyInMemory(MapLocation loc) throws GameActionException{
		int locNumber = Utilities.targetToInt(loc);
		for (int i = min_ally; i <= max_ally; i++){
			if (readBits(min_address + (int) Math.floor((i - min_ally) / 32)) == bits_zero){
				i += 32;
						continue;
				}
			long current_int = readBits(i);
			if (AllyData.getLocInt(current_int) == locNumber){
				return i - min_ally;
			}
		}
		return -1;
	}
	
	public static void pruneAllyMemory() throws Exception{
		System.out.println("Starting defrag");
		System.out.print("Lumberjack: ");
		System.out.println(Globals.getLumberjackCount());
		System.out.print("Scout: ");
		System.out.println(Globals.getScoutCount());
		System.out.print("Tank: ");
		System.out.println(Globals.getTankCount());
		System.out.print("Archon: ");
		System.out.println(Globals.getArchonCount());
		System.out.print("Soldier: ");
		System.out.println(Globals.getSoldierCount());
		for (int i = min_ally; i <= max_ally; i++){
			if (readBits(min_address + (int) Math.floor((i - min_ally) / 32)) == bits_zero){
				i += 32;
				continue;
			}
			long current_int = readBits(i);
			if (current_int != bits_zero){
				// if the alive variable is correct for the current turn
				// then it wasnt updated last turn
				// NOTE freeing memory does not write zeroes to the old location
				if (AllyData.isAlive(current_int) == ((rc.getRoundNum() % 2) == 1)){
					System.out.print("Killing: ");
					System.out.println(Long.toBinaryString(current_int));
					System.out.println(i);
					Globals.incrementUnitCount(AllyData.getType(readBits(i)), -1);
					freeAllyMemory(i - min_ally);
				}
			}
		}
	}
	
	public static void pruneOrders() throws Exception {
		int old_orders = Globals.getOrderCount();
		if (old_orders == 0){
			return;
		}
		long[] Orders = new long[old_orders];
		int order_count = 0;
		int total_orders = 0;
		for (int i = min_order; i <= max_order; i++){
			total_orders++;
			long current = readBits(i);
			if (current == bits_zero){
				break;
			}
			
			if (Order.getCount(current) <= 0 || Order.getTTL(current) <= 0) {
				continue;
			}

			Orders[order_count++] = current;
		}
		for (int i = 0; i < total_orders; i++){
			if (i < order_count){
				writeBits(i + min_order, Orders[i]);
			} else {
				writeBits(i + min_order, 0);
			}
		}
		Globals.setOrderCount(order_count);
	}
	
	public static Order getOrder(int index) throws GameActionException{
		return new Order(readBits(min_order + index));
	}
	
	public static void addOrder(Order o) throws Exception{
		writeBits(min_order + Globals.getOrderCount(), o.toInt());
		Globals.setOrderCount(Globals.getOrderCount() + 1);
	}
	
}
