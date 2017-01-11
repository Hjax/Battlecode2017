package first_bot;

import battlecode.common.GameActionException;

public class Memory extends Bot{
	// we can have at most 19 orders, 490 allies, 490 enemies

	public static int read(int location) throws GameActionException{
		if (Mirror.containsKey(location)) {
			return Mirror.get(location);
		}
		int value = rc.readBroadcast(location);
		Mirror.put(location, value);
		return value;
	}
	
	public static void write(int location, int value) throws GameActionException {
		Mirror.put(location, value);
		Updated.add(location);
	}
	
	public static void commit() throws GameActionException {
		for (Integer i: Updated) {
			rc.broadcast(i, Mirror.get(i));
		}
	}
	
	public static int getNumOrders() throws GameActionException{
		return Tools.bitInterval(read(0), 29, 32);
	}
	
	public static int getNumAllies() throws GameActionException{
		return Tools.bitInterval(read(0), 20, 28);
	}
	
	public static int getNumEnemies() throws GameActionException{
		return Tools.bitInterval(read(0), 11, 19);
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
	
	/**public static void defragAllies() throws GameActionException{
		AllyData[] allies = new AllyData[490];
		int alliesPointer = 0;
		
		for (int i=20; i < 510; i++){
			int value = read(i);
				
			if (value == 0) {
				break;
			}
			
				
			AllyData current = new AllyData(value);
			
			// if their alive value is equal to the current turn % 2 then they didn't update it last turn
			if (current.alive == ((rc.getRoundNum() % 2) == 1)) {
				continue;
			}
			
			allies[alliesPointer++] = current;
		}
		// this is bubblesort 
		for (int i = 0; i < alliesPointer; i++) {
			boolean swapped = false;
			for (int j = 0; j < alliesPointer - i; j++) {
				if (j + 1 < alliesPointer) {
					if (Tools.targetToInt(allies[j].location) < Tools.targetToInt(allies[j + 1].location)) {
						swapped = true;
						AllyData temp = allies[j];
						allies[j] = allies[j + 1];
						allies[j + 1] = temp;
					}
				}
			}
			if (!swapped) {
				break;
			}
		}
		
		for (int i = 0; i < getNumAllies(); i++){
			if (i < alliesPointer) {
				write(i + 20, allies[i].toInt());
			}
			else {
				write(i + 20, 0);
			}
		}
		
	}
	
	public static void runDefrag() {
		// first we load everything into arrays
		KillOrder[] orders = new KillOrder[20];
		AllyData[] allies = new AllyData[490];
		EnemyData[] enemies = new EnemyData[490];
		
		
		for (int i=0; i < 20; i++){
			try {
				orders[i] = readOrder(i);
			} catch (GameActionException e) {
				System.out.println("Weird memory error");
			}
		}
		
		for (int i=0; i < 490; i++){
			try {
				allies[i] = readAlly(i);
				enemies[i] = readEnemy(i);
			} catch (GameActionException e) {
				System.out.println("Weird memory error");
			}
		}
		
	}
	**/
}
