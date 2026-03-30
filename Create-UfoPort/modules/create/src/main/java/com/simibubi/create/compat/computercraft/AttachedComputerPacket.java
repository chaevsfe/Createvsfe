package com.simibubi.create.compat.computercraft;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class AttachedComputerPacket extends BlockEntityDataPacket<SyncedBlockEntity> {

	private final boolean hasAttachedComputer;

	public AttachedComputerPacket(BlockPos blockEntityPos, boolean hasAttachedComputer) {
		super(blockEntityPos);
		this.hasAttachedComputer = hasAttachedComputer;
	}

	public AttachedComputerPacket(RegistryFriendlyByteBuf buf) {
		super(buf);
		this.hasAttachedComputer = buf.readBoolean();
	}

	@Override
	protected void writeData(RegistryFriendlyByteBuf buffer) {
		buffer.writeBoolean(hasAttachedComputer);
	}

	@Override
	protected void handlePacket(SyncedBlockEntity blockEntity) {
		if (blockEntity instanceof SmartBlockEntity sbe) {
			sbe.getBehaviour(AbstractComputerBehaviour.TYPE)
				.setHasAttachedComputer(hasAttachedComputer);
		}
	}

}
