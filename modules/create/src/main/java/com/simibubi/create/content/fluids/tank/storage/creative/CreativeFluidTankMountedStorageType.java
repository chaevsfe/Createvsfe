package com.simibubi.create.content.fluids.tank.storage.creative;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * MountedFluidStorageType for creative fluid tanks.
 */
public class CreativeFluidTankMountedStorageType extends MountedFluidStorageType<CreativeFluidTankMountedStorage> {
	public CreativeFluidTankMountedStorageType() {
		super(CreativeFluidTankMountedStorage.CODEC);
	}

	@Override
	@Nullable
	public CreativeFluidTankMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (!(be instanceof CreativeFluidTankBlockEntity tank)) return null;
		if (!tank.isController()) return null;
		Storage<FluidVariant> storage = tank.getFluidStorage(null);
		if (storage == null) return null;
		return CreativeFluidTankMountedStorage.fromTank(tank);
	}
}
