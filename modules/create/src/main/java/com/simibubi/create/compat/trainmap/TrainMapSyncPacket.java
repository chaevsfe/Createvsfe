package com.simibubi.create.compat.trainmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simibubi.create.AllPackets;
import com.simibubi.create.compat.trainmap.TrainMapSync.TrainMapSyncEntry;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import com.simibubi.create.foundation.utility.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class TrainMapSyncPacket extends SimplePacketBase {

	public boolean light;
	public List<Pair<UUID, TrainMapSyncEntry>> entries = new ArrayList<>();

	public TrainMapSyncPacket(boolean light) {
		this.light = light;
	}

	public TrainMapSyncPacket(RegistryFriendlyByteBuf buf) {
		this.light = buf.readBoolean();
		int size = buf.readVarInt();
		for (int i = 0; i < size; i++) {
			UUID id = UUIDUtil.STREAM_CODEC.decode(buf);
			TrainMapSyncEntry entry = TrainMapSyncEntry.STREAM_CODEC.decode(buf);
			entries.add(Pair.of(id, entry));
		}
	}

	public void add(UUID trainId, TrainMapSyncEntry data) {
		entries.add(Pair.of(trainId, data));
	}

	@Override
	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeBoolean(light);
		buf.writeVarInt(entries.size());
		for (Pair<UUID, TrainMapSyncEntry> pair : entries) {
			UUIDUtil.STREAM_CODEC.encode(buf, pair.getFirst());
			TrainMapSyncEntry.STREAM_CODEC.encode(buf, pair.getSecond());
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> TrainMapSyncClient.receive(this));
		return true;
	}

}
