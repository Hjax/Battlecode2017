package ecoBot;

public class KillOrder {
	public int type;
	public int memloc;
	public int TTL;
	public KillOrder(int memoryData) {
		type = Utilities.bitInterval(memoryData, 29, 32);
		memloc = Utilities.bitInterval(memoryData, 20, 28);
		TTL = Utilities.bitInterval(memoryData, 12, 19);
	}
}
