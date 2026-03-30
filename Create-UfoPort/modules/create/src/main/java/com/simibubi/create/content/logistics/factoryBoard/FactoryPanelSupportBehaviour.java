package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public class FactoryPanelSupportBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<FactoryPanelSupportBehaviour> TYPE = new BehaviourType<>();

	private List<FactoryPanelPosition> linkedPanels;
	private boolean changed;

	private Supplier<Boolean> outputPower;
	private Supplier<Boolean> isOutput;
	private Runnable onNotify;

	public FactoryPanelSupportBehaviour(SmartBlockEntity be, Supplier<Boolean> isOutput, Supplier<Boolean> outputPower,
										Runnable onNotify) {
		super(be);
		this.isOutput = isOutput;
		this.outputPower = outputPower;
		this.onNotify = onNotify;
		linkedPanels = new ArrayList<>();
	}

	public boolean shouldPanelBePowered() {
		return isOutput() && outputPower.get();
	}

	public boolean isOutput() {
		return isOutput.get();
	}

	public void notifyLink() {
		onNotify.run();
	}

	@Override
	public void destroy() {
		for (FactoryPanelPosition panelPos : linkedPanels) {
			if (!getWorld().isLoaded(panelPos.pos()))
				continue;
			FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
			if (behaviour == null)
				continue;
			behaviour.targetedByLinks.remove(getPos());
			behaviour.blockEntity.notifyUpdate();
		}
		super.destroy();
	}

	public void notifyPanels() {
		if (getWorld().isClientSide())
			return;
		for (Iterator<FactoryPanelPosition> iterator = linkedPanels.iterator(); iterator.hasNext(); ) {
			FactoryPanelPosition panelPos = iterator.next();
			if (!getWorld().isLoaded(panelPos.pos()))
				continue;
			FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
			if (behaviour == null) {
				iterator.remove();
				changed = true;
				continue;
			}
			behaviour.checkForRedstoneInput();
		}
	}

	@Nullable
	public Boolean shouldBePoweredTristate() {
		for (Iterator<FactoryPanelPosition> iterator = linkedPanels.iterator(); iterator.hasNext(); ) {
			FactoryPanelPosition panelPos = iterator.next();
			if (!getWorld().isLoaded(panelPos.pos()))
				return null;
			FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
			if (behaviour == null) {
				iterator.remove();
				changed = true;
				continue;
			}
			if (behaviour.isActive() && behaviour.satisfied && behaviour.count != 0)
				return true;
		}
		return false;
	}

	public List<FactoryPanelPosition> getLinkedPanels() {
		return linkedPanels;
	}

	public void connect(FactoryPanelBehaviour panel) {
		FactoryPanelPosition panelPosition = panel.getPanelPosition();
		if (linkedPanels.contains(panelPosition))
			return;
		linkedPanels.add(panelPosition);
		changed = true;
	}

	public void disconnect(FactoryPanelBehaviour panel) {
		linkedPanels.remove(panel.getPanelPosition());
		changed = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (changed) {
			changed = false;
			if (!isOutput())
				notifyLink();
			blockEntity.setChanged();
		}
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, Create.getRegistryAccess());
		Codec.list(FactoryPanelPosition.CODEC).encodeStart(ops, linkedPanels)
			.resultOrPartial(err -> {})
			.ifPresent(encoded -> nbt.put("LinkedGauges", encoded));
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		linkedPanels.clear();
		if (nbt.contains("LinkedGauges")) {
			RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, Create.getRegistryAccess());
			Codec.list(FactoryPanelPosition.CODEC).parse(ops, nbt.get("LinkedGauges"))
				.resultOrPartial(err -> {})
				.ifPresent(linkedPanels::addAll);
		}
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

}
