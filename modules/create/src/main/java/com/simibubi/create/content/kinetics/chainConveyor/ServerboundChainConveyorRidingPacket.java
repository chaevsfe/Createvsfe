package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundChainConveyorRidingPacket extends BlockEntityConfigurationPacket<ChainConveyorBlockEntity> {

	private boolean stop;

	public ServerboundChainConveyorRidingPacket(BlockPos pos, boolean stop) {
		super(pos);
		this.stop = stop;
	}

	public ServerboundChainConveyorRidingPacket(RegistryFriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		buffer.writeBoolean(stop);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		stop = buffer.readBoolean();
	}

	@Override
	protected int maxRange() {
		return AllConfigs.server().kinetics.maxChainConveyorLength.get() * 2;
	}

	@Override
	protected void applySettings(ServerPlayer sender, ChainConveyorBlockEntity be) {
		sender.fallDistance = 0;
		// aboveGroundTickCount and aboveGroundVehicleTickCount are private in Fabric

		if (stop)
			ServerChainConveyorHandler.handleStopRidingPacket(sender);
		else
			ServerChainConveyorHandler.handleTTLPacket(sender);
	}

	@Override
	protected void applySettings(ChainConveyorBlockEntity be) {
		// Not used - applySettings(ServerPlayer, BE) is overridden
	}
}
