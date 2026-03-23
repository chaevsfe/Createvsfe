package com.jozufozu.flywheel.light;

import com.jozufozu.flywheel.util.box.ImmutableBox;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

/**
 * Compat stub for old Flywheel 0.6.x LightVolume.
 */
public class LightVolume {
	public LightVolume(LevelAccessor level, ImmutableBox bounds) {
	}

	public void initialize() {
	}

	public void delete() {
	}

	public void bind() {
	}

	public void unbind() {
	}

	public void move(ImmutableBox newBounds) {
	}

	public short getPackedLight(int x, int y, int z) {
		return 0;
	}

	public void onLightUpdate(LightLayer type, ImmutableBox changed) {
	}

	public AABB toAABB() {
		return new AABB(0, 0, 0, 1, 1, 1);
	}
}
