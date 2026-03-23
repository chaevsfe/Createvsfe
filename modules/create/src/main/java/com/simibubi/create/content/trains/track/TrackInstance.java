package com.simibubi.create.content.trains.track;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;

/**
 * Stub - instanced rendering disabled. BER fallback handles rendering.
 */
public class TrackInstance extends BlockEntityInstance<TrackBlockEntity> {
	public TrackInstance(MaterialManager materialManager, TrackBlockEntity blockEntity) {
		super(materialManager, blockEntity);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void updateLight() {
	}

	@Override
	protected void remove() {
	}
}
