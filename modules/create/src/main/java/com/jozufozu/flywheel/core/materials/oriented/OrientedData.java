package com.jozufozu.flywheel.core.materials.oriented;

import org.joml.Quaternionf;

import com.jozufozu.flywheel.core.materials.BasicData;

import net.minecraft.core.BlockPos;

/**
 * Compat stub for old Flywheel 0.6.x OrientedData.
 */
public class OrientedData extends BasicData {
	public OrientedData setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public OrientedData setPosition(float x, float y, float z) {
		return this;
	}

	public OrientedData setRotation(Quaternionf rotation) {
		return this;
	}

	public OrientedData setPivot(float x, float y, float z) {
		return this;
	}

	public OrientedData nudge(float x, float y, float z) {
		return this;
	}
}
