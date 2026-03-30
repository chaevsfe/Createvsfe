package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;

import io.github.fabricators_of_create.porting_lib_ufo.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A server to client version of {@link BlockEntityConfigurationPacket}
 *
 * @param <BE>
 */
public abstract class BlockEntityDataPacket<BE extends SyncedBlockEntity> extends SimplePacketBase {

	protected BlockPos pos;

	public BlockEntityDataPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
	}

	public BlockEntityDataPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		writeData(buffer);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::handleOnClient));
		return true;
	}

	@Environment(EnvType.CLIENT)
	@SuppressWarnings("unchecked")
	private void handleOnClient() {
		ClientLevel world = Minecraft.getInstance().level;

		if (world == null)
			return;

		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof SyncedBlockEntity) {
			handlePacket((BE) blockEntity);
		}
	}

	protected abstract void writeData(RegistryFriendlyByteBuf buffer);

	protected abstract void handlePacket(BE blockEntity);
}
