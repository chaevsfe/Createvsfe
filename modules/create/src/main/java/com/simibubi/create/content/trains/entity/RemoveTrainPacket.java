package com.simibubi.create.content.trains.entity;

import java.util.UUID;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * S2C packet sent when a train is removed from the client's railway manager.
 * Fabric/UfoPort: Equivalent to NeoForge RemoveTrainPacket.
 */
public class RemoveTrainPacket extends SimplePacketBase {

	private final UUID id;

	public RemoveTrainPacket(UUID id) {
		this.id = id;
	}

	public RemoveTrainPacket(Train train) {
		this(train.id);
	}

	public RemoveTrainPacket(RegistryFriendlyByteBuf buffer) {
		this.id = buffer.readUUID();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeUUID(id);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> CreateClient.RAILWAYS.trains.remove(id));
		return true;
	}
}
