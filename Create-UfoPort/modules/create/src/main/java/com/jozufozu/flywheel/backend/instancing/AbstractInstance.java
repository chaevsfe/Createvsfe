package com.jozufozu.flywheel.backend.instancing;

import java.util.Arrays;
import java.util.stream.Stream;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Compat stub for old Flywheel 0.6.x AbstractInstance.
 */
public abstract class AbstractInstance {
	protected final MaterialManager materialManager;
	public final Level world;

	public AbstractInstance(MaterialManager materialManager, Level world) {
		this.materialManager = materialManager;
		this.world = world;
	}

	public AbstractInstance(MaterialManager materialManager) {
		this.materialManager = materialManager;
		this.world = null;
	}

	public void init() {
	}

	public void update() {
	}

	public void updateLight() {
	}

	protected void remove() {
	}

	public boolean shouldReset() {
		return false;
	}

	protected void relight(BlockPos pos, FlatLit<?>... models) {
		// no-op stub
	}

	@SuppressWarnings("unchecked")
	protected <L extends FlatLit<?>> void relight(BlockPos pos, Stream<L> models) {
		// no-op stub
	}
}
