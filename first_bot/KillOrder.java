package first_bot;

public class KillOrder {
	public int type;
	public int memloc;
	public int TTL;
	public KillOrder(int memoryData) {
		type = Tools.bitInterval(memoryData, 29, 32);
		memloc = Tools.bitInterval(memoryData, 20, 28);
		TTL = Tools.bitInterval(memoryData, 12, 19);
	}
}
