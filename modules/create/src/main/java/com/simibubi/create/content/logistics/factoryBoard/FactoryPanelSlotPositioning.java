package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FactoryPanelSlotPositioning {

	private final PanelSlot slot;

	public FactoryPanelSlotPositioning(PanelSlot slot) {
		this.slot = slot;
	}

	public Vec3 getLocalOffset(Level level, BlockPos pos, BlockState state) {
		float x = (1 - slot.xOffset) * 0.5f - 0.25f;
		float y = (1 - slot.yOffset) * 0.5f - 0.25f;
		return new Vec3(x, 0, y);
	}
}
