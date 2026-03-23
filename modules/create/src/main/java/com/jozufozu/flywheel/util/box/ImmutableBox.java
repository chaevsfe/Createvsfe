package com.jozufozu.flywheel.util.box;

/**
 * Compat stub for old Flywheel 0.6.x ImmutableBox.
 */
public interface ImmutableBox {
	int getMinX();
	int getMinY();
	int getMinZ();
	int getMaxX();
	int getMaxY();
	int getMaxZ();

	default int volume() {
		return (getMaxX() - getMinX()) * (getMaxY() - getMinY()) * (getMaxZ() - getMinZ());
	}
}
