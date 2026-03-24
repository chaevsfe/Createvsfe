package com.simibubi.create.content.logistics.packagerLink;

import java.util.UUID;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Behaviour that links a block entity to the logistics network via a frequency ID.
 * Stub implementation for Phase 3 foundation — full implementation will be added
 * when the PackagerLink wireless network system is ported.
 */
public class LogisticallyLinkedBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<LogisticallyLinkedBehaviour> TYPE = new BehaviourType<>();

	public int redstonePower;
	public UUID freqId;
	private boolean global;

	public static enum RequestType {
		RESTOCK, REDSTONE, PLAYER
	}

	public LogisticallyLinkedBehaviour(SmartBlockEntity be, boolean global) {
		super(be);
		this.global = global;
		this.freqId = UUID.randomUUID();
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		if (nbt.contains("FreqId"))
			freqId = nbt.getUUID("FreqId");
		redstonePower = nbt.getInt("RedstonePower");
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		if (freqId != null)
			nbt.putUUID("FreqId", freqId);
		nbt.putInt("RedstonePower", redstonePower);
	}

	/**
	 * Check if a player may administrate this logistics network (lock/unlock, configure).
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayAdministrate(Player player) {
		return true;
	}

	/**
	 * Check if a player may interact with this logistics network (place orders).
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayInteract(Player player) {
		return true;
	}

	/**
	 * Check interaction permission and send status message if denied.
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayInteractMessage(Player player) {
		return true;
	}

	/**
	 * Called when redstone power level changes for this link.
	 */
	public void redstonePowerChanged(int power) {
		this.redstonePower = power;
	}
}
