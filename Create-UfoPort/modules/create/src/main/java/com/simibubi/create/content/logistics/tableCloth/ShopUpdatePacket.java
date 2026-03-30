package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ShopUpdatePacket extends SimplePacketBase {

	private final BlockPos pos;

	public ShopUpdatePacket(BlockPos pos) {
		this.pos = pos;
	}

	public ShopUpdatePacket(RegistryFriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(this::handleClient);
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void handleClient() {
		Level level = Minecraft.getInstance().level;
		if (level == null)
			return;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TableClothBlockEntity tcbe && tcbe.hasLevel())
			tcbe.notifyUpdate();
	}
}
