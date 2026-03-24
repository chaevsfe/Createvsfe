package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FactoryPanelSlotPositioning {

	public PanelSlot slot;

	public FactoryPanelSlotPositioning(PanelSlot slot) {
		this.slot = slot;
	}

	public Vec3 getLocalOffset(Level level, BlockPos pos, BlockState state) {
		return getCenterOfSlot(state, slot);
	}

	public static Vec3 getCenterOfSlot(BlockState state, PanelSlot slot) {
		Vec3 vec = new Vec3(.25 + slot.xOffset * .5, 1.5 / 16f, .25 + slot.yOffset * .5);
		vec = VecHelper.rotateCentered(vec, 180, Axis.Y);
		vec = VecHelper.rotateCentered(vec, Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(state) + 90, Axis.X);
		vec = VecHelper.rotateCentered(vec, Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(state), Axis.Y);
		return vec;
	}
}
