package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelState;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelType;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FactoryPanelBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<FactoryPanelBehaviour> TYPE = new BehaviourType<>();

	public PanelSlot slot;
	public PanelType type;
	public PanelState panelState;
	public ItemStack filter;
	public int count;
	public String address;
	public UUID networkId;

	public List<FactoryPanelConnection> connections;
	public RequestPromiseQueue restockerPromises;

	private FactoryPanelSlotPositioning slotPositioning;

	public FactoryPanelBehaviour(SmartBlockEntity be, PanelSlot slot) {
		super(be);
		this.slot = slot;
		this.type = PanelType.NETWORK;
		this.panelState = PanelState.PASSIVE;
		this.filter = ItemStack.EMPTY;
		this.count = 1;
		this.address = "";
		this.connections = new ArrayList<>();
		this.restockerPromises = new RequestPromiseQueue(() -> {});
	}

	public boolean isActive() {
		return panelState != PanelState.PASSIVE && !filter.isEmpty();
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		readPanel(nbt);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		writePanel(nbt);
	}

	public void readPanel(CompoundTag nbt) {
		type = nbt.contains("Type") ? PanelType.values()[nbt.getInt("Type")] : PanelType.NETWORK;
		panelState = nbt.contains("State") ? PanelState.values()[nbt.getInt("State")] : PanelState.PASSIVE;
		count = nbt.getInt("Count");
		address = nbt.getString("Address");
		if (nbt.contains("NetworkId"))
			networkId = nbt.getUUID("NetworkId");
	}

	public void writePanel(CompoundTag nbt) {
		nbt.putInt("Type", type.ordinal());
		nbt.putInt("State", panelState.ordinal());
		nbt.putInt("Count", count);
		nbt.putString("Address", address);
		if (networkId != null)
			nbt.putUUID("NetworkId", networkId);
	}

	public FactoryPanelSlotPositioning getSlotPositioning() {
		if (slotPositioning == null)
			slotPositioning = new FactoryPanelSlotPositioning(slot);
		return slotPositioning;
	}

	@Nullable
	public static FactoryPanelBehaviour at(Level level, FactoryPanelPosition pos) {
		if (!(level.getBlockEntity(pos.pos()) instanceof FactoryPanelBlockEntity fpbe))
			return null;
		return fpbe.panels.get(pos.slot());
	}
}
