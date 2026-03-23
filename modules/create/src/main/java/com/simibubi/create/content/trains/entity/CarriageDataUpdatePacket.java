package com.simibubi.create.content.trains.entity;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import io.github.fabricators_of_create.porting_lib_ufo.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class CarriageDataUpdatePacket extends SimplePacketBase {

	private int entity;
	private CarriageSyncData data;

	public CarriageDataUpdatePacket(CarriageContraptionEntity entity) {
		this.entity = entity.getId();
		this.data = entity.carriageData;
	}

	public CarriageDataUpdatePacket(RegistryFriendlyByteBuf buf) {
		this.entity = buf.readVarInt();
		this.data = new CarriageSyncData();
		this.data.read(buf);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(entity);
		this.data.write(buffer);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::handleOnClient));
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void handleOnClient() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null)
			return;
		Entity entity = mc.level.getEntity(this.entity);
		if (entity instanceof CarriageContraptionEntity carriage) {
			carriage.onCarriageDataUpdate(this.data);
		} else {
			Create.LOGGER.error("Invalid CarriageDataUpdatePacket for non-carriage entity: " + entity);
		}
	}
}
