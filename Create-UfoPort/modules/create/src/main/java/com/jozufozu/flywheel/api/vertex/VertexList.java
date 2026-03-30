package com.jozufozu.flywheel.api.vertex;

/**
 * Compat stub for old Flywheel 0.6.x VertexList.
 */
public interface VertexList {
	float getX(int index);
	float getY(int index);
	float getZ(int index);
	byte getR(int index);
	byte getG(int index);
	byte getB(int index);
	byte getA(int index);
	float getU(int index);
	float getV(int index);
	float getNX(int index);
	float getNY(int index);
	float getNZ(int index);
	int getLight(int index);
	int getVertexCount();

	default boolean isEmpty() {
		return getVertexCount() == 0;
	}

	default void delete() {
		// no-op
	}
}
