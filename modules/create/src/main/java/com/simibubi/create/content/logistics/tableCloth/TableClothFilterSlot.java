package com.simibubi.create.content.logistics.tableCloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.VecHelper;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

class TableClothFilterSlot extends ValueBoxTransform {

	private TableClothBlockEntity be;

	public TableClothFilterSlot(TableClothBlockEntity be) {
		this.be = be;
	}

	@Override
	public Vec3 getLocalOffset(BlockState state) {
		Vec3 v = be.sideOccluded ? VecHelper.voxelSpace(8, 0.75, 15.25) : VecHelper.voxelSpace(12, -2.75, 16.75);
		return VecHelper.rotateCentered(v, -be.facing.toYRot(), Axis.Y);
	}

	@Override
	public void rotate(BlockState state, PoseStack ms) {
		TransformStack.of(ms)
			.rotateYDegrees(180 - be.facing.toYRot())
			.rotateXDegrees(be.sideOccluded ? 90 : 0);
	}

}
