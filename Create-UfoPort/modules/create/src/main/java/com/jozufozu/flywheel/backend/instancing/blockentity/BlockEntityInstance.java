package com.jozufozu.flywheel.backend.instancing.blockentity;

import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.AbstractInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compat stub for old Flywheel 0.6.x BlockEntityInstance.
 */
public abstract class BlockEntityInstance<T extends BlockEntity> extends AbstractInstance {
	protected final T blockEntity;
	protected final BlockPos pos;
	protected final BlockPos instancePos;
	protected final BlockState blockState;

	public BlockEntityInstance(MaterialManager materialManager, T blockEntity) {
		super(materialManager, blockEntity.getLevel());
		this.blockEntity = blockEntity;
		this.pos = blockEntity.getBlockPos();
		this.instancePos = pos;
		this.blockState = blockEntity.getBlockState();
	}

	public BlockPos getInstancePosition() {
		return instancePos;
	}

	public boolean shouldReset() {
		return blockEntity.getBlockState() != blockState;
	}

	public Material<ModelData> getTransformMaterial() {
		return materialManager.defaultSolid().material(Materials.TRANSFORMED);
	}

	public Material<OrientedData> getOrientedMaterial() {
		return materialManager.defaultCutout().material(Materials.ORIENTED);
	}
}
