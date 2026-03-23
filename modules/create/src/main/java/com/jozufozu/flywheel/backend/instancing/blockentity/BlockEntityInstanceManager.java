package com.jozufozu.flywheel.backend.instancing.blockentity;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.TaskEngine;

import net.minecraft.client.Camera;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Compat stub for old Flywheel 0.6.x BlockEntityInstanceManager.
 * This version is in the blockentity package as expected by ContraptionInstanceManager.
 */
public class BlockEntityInstanceManager {
	protected final MaterialManager materialManager;

	public BlockEntityInstanceManager(MaterialManager materialManager) {
		this.materialManager = materialManager;
	}

	public void add(BlockEntity be) {
	}

	public void update(BlockEntity be) {
	}

	public void remove(BlockEntity be) {
	}

	public void invalidate() {
	}

	public void beginFrame(TaskEngine taskEngine, Camera info) {
	}

	public void tick() {
	}

	public void detachLightListeners() {
	}

	protected boolean canCreateInstance(BlockEntity blockEntity) {
		return true;
	}

	protected void updateInstance(DynamicInstance dyn, float lookX, float lookY, float lookZ, int cX, int cY, int cZ) {
	}
}
