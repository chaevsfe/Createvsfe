package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WiFiEffectPacket extends SimplePacketBase {

	private final BlockPos pos;

	public WiFiEffectPacket(BlockPos pos) {
		this.pos = pos;
	}

	public WiFiEffectPacket(RegistryFriendlyByteBuf buffer) {
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
		Level level = Minecraft.getInstance().level;
		if (level == null)
			return;
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof PackagerLinkBlockEntity plbe)
			plbe.playEffect();
		if (blockEntity instanceof StockTickerBlockEntity stbe)
			stbe.playEffect();
	}

	public static void send(Level level, BlockPos pos) {
		if (level instanceof ServerLevel serverLevel) {
			WiFiEffectPacket packet = new WiFiEffectPacket(pos);
			for (ServerPlayer player : serverLevel.getPlayers(p -> p.blockPosition().closerThan(pos, 32))) {
				com.simibubi.create.AllPackets.getChannel().sendToClient(packet, player);
			}
		}
	}
}
