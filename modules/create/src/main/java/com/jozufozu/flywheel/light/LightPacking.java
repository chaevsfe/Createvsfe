package com.jozufozu.flywheel.light;

/**
 * Compat stub for old Flywheel 0.6.x LightPacking.
 */
public class LightPacking {
	public static int packLight(int blockLight, int skyLight) {
		return (blockLight & 0xF) | ((skyLight & 0xF) << 4);
	}

	public static int getBlock(int packed) {
		return packed & 0xF;
	}

	public static int getSky(int packed) {
		return (packed >> 4) & 0xF;
	}

	public static int getBlock(short packed) {
		return packed & 0xF;
	}

	public static int getSky(short packed) {
		return (packed >> 4) & 0xF;
	}
}
