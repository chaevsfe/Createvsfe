package com.simibubi.create.content.fluids.tank.storage;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * MountedFluidStorageType for fluid tanks.
 * Mounts the fluid tank's Storage<FluidVariant> for use in contraptions.
 */
public class FluidTankMountedStorageType extends MountedFluidStorageType<FluidTankMountedStorage> {
	public FluidTankMountedStorageType() {
		super(FluidTankMountedStorage.CODEC);
	}

	@Override
	@Nullable
	public FluidTankMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (!(be instanceof FluidTankBlockEntity tank)) return null;
		if (!tank.isController()) return null;
		Storage<FluidVariant> storage = tank.getFluidStorage(null);
		if (storage == null) return null;
		return FluidTankMountedStorage.fromTank(tank);
	}

	public static MapCodec<FluidTankMountedStorage> codec() {
		return FluidTankMountedStorage.CODEC;
	}
}
