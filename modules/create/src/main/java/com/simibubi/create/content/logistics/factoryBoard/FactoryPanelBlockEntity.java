package com.simibubi.create.content.logistics.factoryBoard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryPanelBlockEntity extends SmartBlockEntity {

	public Map<PanelSlot, FactoryPanelBehaviour> panels;
	public boolean restocker;

	public FactoryPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		panels = new HashMap<>();
		restocker = false;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		for (PanelSlot slot : PanelSlot.values()) {
			FactoryPanelBehaviour panel = new FactoryPanelBehaviour(this, slot);
			panels.put(slot, panel);
			behaviours.add(panel);
		}
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		CompoundTag panelsTag = new CompoundTag();
		for (Map.Entry<PanelSlot, FactoryPanelBehaviour> entry : panels.entrySet()) {
			CompoundTag panelTag = new CompoundTag();
			entry.getValue().writePanel(panelTag);
			panelsTag.put(entry.getKey().getSerializedName(), panelTag);
		}
		tag.put("Panels", panelsTag);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (tag.contains("Panels")) {
			CompoundTag panelsTag = tag.getCompound("Panels");
			for (Map.Entry<PanelSlot, FactoryPanelBehaviour> entry : panels.entrySet()) {
				String key = entry.getKey().getSerializedName();
				if (panelsTag.contains(key))
					entry.getValue().readPanel(panelsTag.getCompound(key));
			}
		}
	}
}
