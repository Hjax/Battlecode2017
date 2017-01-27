package newMemory;

import battlecode.common.*;

public class Debug {
	
	public static int bytecode;

	public static void debug_print(String output) {
		System.out.println(output);
	}
	
	public static void debug_print(int output) {
		System.out.println(output);
	}
	
	public static void debug_print(float output) {
		System.out.println(output);
	}
	
	public static void debug_bytecode_start() {
		bytecode = Clock.getBytecodeNum();
	}
	
	public static void debug_bytecode_end(String message) {
		System.out.println("Timer with label: " + message + " used " + Integer.toString(Clock.getBytecodeNum() - bytecode) + " bytecode.");
	}
	
	
}
