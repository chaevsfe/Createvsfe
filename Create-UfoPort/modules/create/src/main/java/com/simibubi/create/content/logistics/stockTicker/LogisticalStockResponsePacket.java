package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class LogisticalStockResponsePacket extends SimplePacketBase {

	private final boolean lastPacket;
	private final BlockPos pos;
	private final List<BigItemStack> items;

	public LogisticalStockResponsePacket(boolean lastPacket, BlockPos pos, List<BigItemStack> items) {
		this.lastPacket = lastPacket;
		this.pos = pos;
		this.items = items;
	}

	public LogisticalStockResponsePacket(RegistryFriendlyByteBuf buffer) {
		lastPacket = buffer.readBoolean();
		pos = buffer.readBlockPos();
		int count = buffer.readVarInt();
		items = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			items.add(BigItemStack.STREAM_CODEC.decode(buffer));
		}
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeBoolean(lastPacket);
		buffer.writeBlockPos(pos);
		buffer.writeVarInt(items.size());
		for (BigItemStack item : items) {
			BigItemStack.STREAM_CODEC.encode(buffer, item);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().level != null
				&& Minecraft.getInstance().level.getBlockEntity(pos) instanceof StockTickerBlockEntity stbe) {
				stbe.receiveStockPacket(items, lastPacket);
			}
		});
		return true;
	}
}
