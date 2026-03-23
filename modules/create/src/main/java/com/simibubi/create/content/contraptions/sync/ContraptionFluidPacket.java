package com.simibubi.create.content.contraptions.sync;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib_ufo.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ContraptionFluidPacket extends SimplePacketBase {

	private int entityId;
	private BlockPos localPos;
	private FluidStack containedFluid;

	public ContraptionFluidPacket(int entityId, BlockPos localPos, FluidStack containedFluid) {
		this.entityId = entityId;
		this.localPos = localPos;
		this.containedFluid = containedFluid;
	}

	public ContraptionFluidPacket(RegistryFriendlyByteBuf buffer) {
		entityId = buffer.readInt();
		localPos = buffer.readBlockPos();
		containedFluid = FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer);
		//containedFluid = FluidStack.readFromPacket(buffer);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeBlockPos(localPos);
		FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, containedFluid);
		//containedFluid.writeToPacket(buffer);
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
		Entity entityByID = Minecraft.getInstance().level.getEntity(entityId);
		if (!(entityByID instanceof AbstractContraptionEntity contraptionEntity))
			return;
		contraptionEntity.getContraption().handleContraptionFluidPacket(localPos, containedFluid);
	}
}
