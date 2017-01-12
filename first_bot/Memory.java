package first_bot;

import battlecode.common.GameActionException;

public class Memory extends Bot{
	// we can have at most 19 orders, 490 allies, 490 enemies

	public static int read(int location) throws GameActionException{
		return rc.readBroadcast(location);
	}
	
	public static void write(int location, int value) throws GameActionException {
		rc.broadcast(location, value);
	}
	
	public static int getNumOrders() throws GameActionException{
		return Utilities.bitInterval(read(0), 29, 32);
	}
	
	public static int getNumAllies() throws GameActionException{
		return Utilities.bitInterval(read(0), 20, 28);
	}
	
	public static int getNumEnemies() throws GameActionException{
		return Utilities.bitInterval(read(0), 11, 19);
	}
	
	public static void setPointers(int numorders, int numallies, int numenemies) throws GameActionException {
		write(0, numorders * (int) Math.pow(2, 29) + numallies * (int) Math.pow(2, 20) + numenemies * (int) Math.pow(2, 11));
	}
	
	public static void setNumOrders(int value) throws GameActionException {
		write(0, value * (int) Math.pow(2, 29) + getNumAllies() * (int) Math.pow(2, 20) + getNumEnemies() * (int) Math.pow(2, 11));
	}
	
	public static void setNumAllies(int value) throws GameActionException {
		write(0, getNumOrders() * (int) Math.pow(2, 29) + value * (int) Math.pow(2, 20) + getNumEnemies() * (int) Math.pow(2, 11));
	}
	
	public static void setNumEnemies(int value) throws GameActionException {
		write(0, getNumOrders() * (int) Math.pow(2, 29) + getNumAllies() * (int) Math.pow(2, 20) + value * (int) Math.pow(2, 11));
	}
	
	public static KillOrder readOrder(int index) throws GameActionException {
		return new KillOrder(read(index));
	}
	
	public static AllyData readAlly(int index) throws GameActionException{
		return new AllyData(read(index + 20));
	}
	
	public static EnemyData readEnemy(int index) throws GameActionException{
		return new EnemyData(read(index + 20 + 490));
	}
	
	public static void defragAllies() throws GameActionException{
		
		for (int i = 20; i < 25; i++){
			System.out.println(Integer.toBinaryString(Memory.read(i)));
		}
		
		int[] allies = new int[490];
		int alliesPointer = 0;
		int totalAllies = 0;

		for (int i=20; i < 510; i++){
			int value = read(i);
				
			if (value == 0) {
				break;
			}
			
			totalAllies++;
			
			// if their alive value is equal to the current turn % 2 then they didn't update it last turn
			if (((value & 2) / 2) == (rc.getRoundNum() % 2)) {
				continue;
			}
			
			allies[alliesPointer++] = value;
		}

		Memory.setNumAllies(alliesPointer);
		
		// this is bubblesort 
		for (int i = 0; i < alliesPointer; i++) {
			boolean swapped = false;
			for (int j = 0; j < alliesPointer - i; j++) {
				if (j + 1 < alliesPointer) {
					if (Utilities.bitInterval(allies[j], 11, 28) > Utilities.bitInterval(allies[j + 1], 11, 28)) {
						swapped = true;
						int temp = allies[j];
						allies[j] = allies[j + 1];
						allies[j + 1] = temp;
					}
				}
			}
			if (!swapped) {
				break;
			}
		}
		for (int i = 0; i < totalAllies; i++){
			if (i < alliesPointer) {
				write(i + 20, allies[i]);
			}
			else {
				write(i + 20, 0);
			}
		}
		for (int i = 20; i < 25; i++){
			System.out.println(Integer.toBinaryString(Memory.read(i)));
		}
	}
}
