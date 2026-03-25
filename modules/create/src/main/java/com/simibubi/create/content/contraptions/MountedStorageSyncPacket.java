package com.simibubi.create.content.contraptions;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import io.github.fabricators_of_create.porting_lib_ufo.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;

/**
 * S2C packet to sync mounted item and fluid storage contents to clients.
 * Sent when contraption storages are dirty and need client-side update.
 */
public class MountedStorageSyncPacket extends SimplePacketBase {
	private final int contraptionId;
	private final Map<BlockPos, MountedItemStorage> items;
	private final Map<BlockPos, MountedFluidStorage> fluids;

	public MountedStorageSyncPacket(int contraptionId, Map<BlockPos, MountedItemStorage> items,
		Map<BlockPos, MountedFluidStorage> fluids) {
		this.contraptionId = contraptionId;
		this.items = items;
		this.fluids = fluids;
	}

	public MountedStorageSyncPacket(RegistryFriendlyByteBuf buffer) {
		contraptionId = buffer.readInt();
		int itemCount = buffer.readVarInt();
		items = new HashMap<>();
		for (int i = 0; i < itemCount; i++) {
			BlockPos pos = buffer.readBlockPos();
			MountedItemStorage storage = MountedItemStorage.STREAM_CODEC.decode(buffer);
			items.put(pos, storage);
		}
		int fluidCount = buffer.readVarInt();
		fluids = new HashMap<>();
		for (int i = 0; i < fluidCount; i++) {
			BlockPos pos = buffer.readBlockPos();
			MountedFluidStorage storage = MountedFluidStorage.STREAM_CODEC.decode(buffer);
			fluids.put(pos, storage);
		}
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeInt(contraptionId);
		buffer.writeVarInt(items.size());
		items.forEach((pos, storage) -> {
			buffer.writeBlockPos(pos);
			MountedItemStorage.STREAM_CODEC.encode(buffer, storage);
		});
		buffer.writeVarInt(fluids.size());
		fluids.forEach((pos, storage) -> {
			buffer.writeBlockPos(pos);
			MountedFluidStorage.STREAM_CODEC.encode(buffer, storage);
		});
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::handleOnClient));
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void handleOnClient() {
		if (Minecraft.getInstance().level == null)
			return;
		Entity entity = Minecraft.getInstance().level.getEntity(contraptionId);
		if (!(entity instanceof AbstractContraptionEntity contraptionEntity))
			return;
		// Notify the contraption that storage data has been synced
		// The contraption may apply item/fluid storage updates on the client side
		contraptionEntity.getContraption().onStorageSyncPacket(this);
	}

	public int getContraptionId() {
		return contraptionId;
	}

	public Map<BlockPos, MountedItemStorage> getItems() {
		return items;
	}

	public Map<BlockPos, MountedFluidStorage> getFluids() {
		return fluids;
	}
}
