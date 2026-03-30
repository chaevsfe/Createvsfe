package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

// C2S: client sends selected target and placement pos to server
public class PackagePortPlacementPacket extends SimplePacketBase {

	private final PackagePortTarget target;
	private final BlockPos pos;

	public PackagePortPlacementPacket(PackagePortTarget target, BlockPos pos) {
		this.target = target;
		this.pos = pos;
	}

	public PackagePortPlacementPacket(RegistryFriendlyByteBuf buffer) {
		this.target = PackagePortTarget.STREAM_CODEC.decode(buffer);
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		PackagePortTarget.STREAM_CODEC.encode(buffer, target);
		buffer.writeBlockPos(pos);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null)
				return;
			Level world = player.level();
			if (!world.isLoaded(pos))
				return;
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (!(blockEntity instanceof PackagePortBlockEntity ppbe))
				return;
			if (!target.canSupport(ppbe))
				return;

			Vec3 targetLocation = target.getExactTargetLocation(ppbe, world, pos);
			if (targetLocation == Vec3.ZERO || !targetLocation.closerThan(Vec3.atBottomCenterOf(pos),
				AllConfigs.server().logistics.packagePortRange.get() + 2))
				return;

			target.setup(ppbe, world, pos);
			ppbe.target = target;
			ppbe.notifyUpdate();
			ppbe.use(player);
		});
		return true;
	}

	// S2C: server notifies client that placement succeeded (triggers flushSettings)
	public static class ClientBoundRequest extends SimplePacketBase {

		private final BlockPos pos;

		public ClientBoundRequest(BlockPos pos) {
			this.pos = pos;
		}

		public ClientBoundRequest(RegistryFriendlyByteBuf buffer) {
			this.pos = buffer.readBlockPos();
		}

		@Override
		public void write(RegistryFriendlyByteBuf buffer) {
			buffer.writeBlockPos(pos);
		}

		@Override
		public boolean handle(Context context) {
			context.enqueueWork(() -> handleClient());
			return true;
		}

		@Environment(EnvType.CLIENT)
		private void handleClient() {
			PackagePortTargetSelectionHandler.flushSettings(pos);
		}
	}
}
