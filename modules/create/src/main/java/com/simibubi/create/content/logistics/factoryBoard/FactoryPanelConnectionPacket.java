package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class FactoryPanelConnectionPacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {

	private FactoryPanelPosition fromPos;
	private FactoryPanelPosition toPos;
	private boolean relocate;

	public FactoryPanelConnectionPacket(FactoryPanelPosition fromPos, FactoryPanelPosition toPos, boolean relocate) {
		super(toPos.pos());
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.relocate = relocate;
	}

	public FactoryPanelConnectionPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		FactoryPanelPosition.STREAM_CODEC.encode(buffer, fromPos);
		FactoryPanelPosition.STREAM_CODEC.encode(buffer, toPos);
		buffer.writeBoolean(relocate);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		fromPos = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		toPos = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		relocate = buffer.readBoolean();
	}

	@Override
	protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
		FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(be.getLevel(), toPos);
		if (behaviour != null)
			if (relocate)
				behaviour.moveTo(fromPos, player);
			else
				behaviour.addConnection(fromPos);
	}

	@Override
	protected void applySettings(FactoryPanelBlockEntity be) {}

	@Override
	protected int maxRange() {
		return super.maxRange() * 2;
	}

}
