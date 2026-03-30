package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class RedstoneRequesterEffectPacket extends SimplePacketBase {

	private final BlockPos pos;
	private final boolean success;

	public RedstoneRequesterEffectPacket(BlockPos pos, boolean success) {
		this.pos = pos;
		this.success = success;
	}

	public RedstoneRequesterEffectPacket(RegistryFriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.success = buffer.readBoolean();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(success);
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
		if (level.getBlockEntity(pos) instanceof RedstoneRequesterBlockEntity rrbe)
			rrbe.playEffect(success);
	}

	public static void send(Level level, BlockPos pos, boolean success) {
		if (level instanceof ServerLevel serverLevel) {
			RedstoneRequesterEffectPacket packet = new RedstoneRequesterEffectPacket(pos, success);
			for (ServerPlayer player : serverLevel.getPlayers(p -> p.blockPosition().closerThan(pos, 32)))
				com.simibubi.create.AllPackets.getChannel().sendToClient(packet, player);
		}
	}
}
