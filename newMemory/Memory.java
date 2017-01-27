package newMemory;

import battlecode.common.*;

public class Memory extends Bot{
	
	private static int min_global = 0;
	private static int min_order = 50;
	private static long bits_zero = (long) Math.pow(2, 31);
	
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
	
	public static void pruneOrders() throws Exception {
		int old_orders = Globals.getOrderCount();
		if (old_orders == 0){
			return;
		}
		long[] Orders = new long[old_orders];
		int order_count = 0;
		int total_orders = 0;
		for (int i = min_order; i <= min_order + old_orders; i++){
			total_orders++;
			long current = readBits(i);
			if (current == bits_zero){
				break;
			}
			
			if (Order.getTTL(current) < rc.getRoundNum()) {
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
	
	public static void deleteOrder(Order o) throws GameActionException {
		o.TTL = 0;
		writeBits(o.index + min_order, o.toLong());
	}
	
	public static long getOrder(int index) throws GameActionException{
		return readBits(min_order + index);
	}
	
	public static void addOrder(Order o) throws Exception{
		writeBits(min_order + Globals.getOrderCount(), o.toLong());
		Globals.setOrderCount(Globals.getOrderCount() + 1);
	}
	
}
