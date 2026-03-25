package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PackageOrderRequestPacket extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {

	private PackageOrderWithCrafts order;
	private String address;
	private boolean encodeRequester;

	public PackageOrderRequestPacket(BlockPos pos, PackageOrderWithCrafts order, String address, boolean encodeRequester) {
		super(pos);
		this.order = order;
		this.address = address;
		this.encodeRequester = encodeRequester;
	}

	public PackageOrderRequestPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		PackageOrderWithCrafts.STREAM_CODEC.encode(buffer, order);
		buffer.writeUtf(address);
		buffer.writeBoolean(encodeRequester);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		order = PackageOrderWithCrafts.STREAM_CODEC.decode(buffer);
		address = buffer.readUtf();
		encodeRequester = buffer.readBoolean();
	}

	@Override
	protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
		if (encodeRequester) {
			if (!order.isEmpty())
				AllSoundEvents.CONFIRM.playOnServer(be.getLevel(), pos);
			player.closeContainer();
			// RedstoneRequesterBlock.programRequester — deferred until Redstone Requester is ported
			return;
		}

		if (!order.isEmpty()) {
			AllSoundEvents.STOCK_TICKER_REQUEST.playOnServer(be.getLevel(), pos);
			// AllAdvancements.STOCK_TICKER.awardTo(player) — deferred until advancements ported
			WiFiEffectPacket.send(be.getLevel(), pos);
		}

		be.broadcastPackageRequest(RequestType.PLAYER, order, null, address);
	}

	@Override
	protected void applySettings(StockTickerBlockEntity be) {}
}
