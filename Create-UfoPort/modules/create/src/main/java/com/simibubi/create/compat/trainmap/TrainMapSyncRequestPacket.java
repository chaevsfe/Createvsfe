package com.simibubi.create.compat.trainmap;

import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class TrainMapSyncRequestPacket extends SimplePacketBase {

	public static final TrainMapSyncRequestPacket INSTANCE = new TrainMapSyncRequestPacket();

	public TrainMapSyncRequestPacket() {}

	public TrainMapSyncRequestPacket(RegistryFriendlyByteBuf buf) {}

	@Override
	public void write(RegistryFriendlyByteBuf buf) {}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			if (context.getSender() != null)
				TrainMapSync.requestReceived(context.getSender());
		});
		return true;
	}

}
