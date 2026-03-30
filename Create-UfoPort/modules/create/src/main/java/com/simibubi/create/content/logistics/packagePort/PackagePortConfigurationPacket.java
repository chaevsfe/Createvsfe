package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PackagePortConfigurationPacket extends BlockEntityConfigurationPacket<PackagePortBlockEntity> {

	private String newFilter;
	private boolean acceptPackages;

	public PackagePortConfigurationPacket(BlockPos pos, String newFilter, boolean acceptPackages) {
		super(pos);
		this.newFilter = newFilter;
		this.acceptPackages = acceptPackages;
	}

	public PackagePortConfigurationPacket(RegistryFriendlyByteBuf buf) {
		super(buf);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		buffer.writeUtf(newFilter);
		buffer.writeBoolean(acceptPackages);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		newFilter = buffer.readUtf();
		acceptPackages = buffer.readBoolean();
	}

	@Override
	protected void applySettings(PackagePortBlockEntity be) {
		if (be.addressFilter.equals(newFilter) && be.acceptsPackages == acceptPackages)
			return;
		be.addressFilter = newFilter;
		be.acceptsPackages = acceptPackages;
		be.filterChanged();
		be.notifyUpdate();
	}
}
