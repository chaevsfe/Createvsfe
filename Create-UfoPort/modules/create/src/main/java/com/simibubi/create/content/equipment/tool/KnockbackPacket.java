package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class KnockbackPacket extends SimplePacketBase {

	private final float yRot;
	private final float strength;

	public KnockbackPacket(float yRot, float strength) {
		this.yRot = yRot;
		this.strength = strength;
	}

	public KnockbackPacket(RegistryFriendlyByteBuf buf) {
		this.yRot = buf.readFloat();
		this.strength = buf.readFloat();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeFloat(yRot);
		buf.writeFloat(strength);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Player player = Minecraft.getInstance().player;
			if (player != null)
				CardboardSwordItem.knockback(player, strength, yRot);
		});
		return true;
	}

	public static void sendTo(ServerPlayer player, float yRot, float strength) {
		AllPackets.getChannel().sendToClient(new KnockbackPacket(yRot, strength), player);
	}
}
