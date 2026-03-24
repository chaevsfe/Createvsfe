package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryPanelEffectPacket extends SimplePacketBase {

	private final FactoryPanelPosition fromPos;
	private final FactoryPanelPosition toPos;
	private final boolean success;

	public FactoryPanelEffectPacket(FactoryPanelPosition fromPos, FactoryPanelPosition toPos, boolean success) {
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.success = success;
	}

	public FactoryPanelEffectPacket(RegistryFriendlyByteBuf buffer) {
		this.fromPos = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		this.toPos = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		this.success = buffer.readBoolean();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		FactoryPanelPosition.STREAM_CODEC.encode(buffer, fromPos);
		FactoryPanelPosition.STREAM_CODEC.encode(buffer, toPos);
		buffer.writeBoolean(success);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleClient());
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void handleClient() {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null)
			return;
		BlockState blockState = level.getBlockState(fromPos.pos());
		if (!AllBlocks.FACTORY_GAUGE.has(blockState))
			return;
		FactoryPanelBehaviour panelBehaviour = FactoryPanelBehaviour.at(level, toPos);
		if (panelBehaviour != null) {
			panelBehaviour.bulb.setValue(1);
			FactoryPanelConnection connection = panelBehaviour.targetedBy.get(fromPos);
			if (connection != null)
				connection.success = success;
		}
	}

	public static void send(Level level, FactoryPanelPosition fromPos, FactoryPanelPosition toPos, boolean success) {
		if (level instanceof ServerLevel serverLevel) {
			FactoryPanelEffectPacket packet = new FactoryPanelEffectPacket(fromPos, toPos, success);
			for (ServerPlayer player : serverLevel.getPlayers(p -> p.blockPosition().closerThan(fromPos.pos(), 32)))
				AllPackets.getChannel().sendToClient(packet, player);
		}
	}
}
