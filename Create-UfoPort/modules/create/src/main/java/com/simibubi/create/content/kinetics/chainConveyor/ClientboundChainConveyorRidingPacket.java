package com.simibubi.create.content.kinetics.chainConveyor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ClientboundChainConveyorRidingPacket extends SimplePacketBase {

	private final Set<UUID> uuids;

	public ClientboundChainConveyorRidingPacket(Collection<UUID> uuids) {
		this.uuids = new HashSet<>(uuids);
	}

	public ClientboundChainConveyorRidingPacket(RegistryFriendlyByteBuf buf) {
		int size = buf.readVarInt();
		uuids = new HashSet<>(size);
		for (int i = 0; i < size; i++)
			uuids.add(buf.readUUID());
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(uuids.size());
		for (UUID uuid : uuids)
			buffer.writeUUID(uuid);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> PlayerSkyhookRenderer.updatePlayerList(uuids));
		return true;
	}
}
