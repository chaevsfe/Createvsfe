package com.simibubi.create.content.fluids.tank.storage.creative;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorage;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mounted fluid storage for creative fluid tanks.
 * Like FluidTankMountedStorage but uses the creative tank storage type.
 */
public class CreativeFluidTankMountedStorage extends FluidTankMountedStorage {
	public static final MapCodec<CreativeFluidTankMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		FluidStack.CODEC.optionalFieldOf("Fluid", FluidStack.EMPTY).forGetter(s -> s.fluidStack),
		Codec.LONG.optionalFieldOf("Capacity", 0L).forGetter(s -> s.capacity)
	).apply(i, CreativeFluidTankMountedStorage::new));

	protected CreativeFluidTankMountedStorage(FluidStack fluidStack, long capacity) {
		super(AllMountedStorageTypes.CREATIVE_FLUID_TANK.get(), fluidStack, capacity);
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (be instanceof FluidTankBlockEntity tankBE && tankBE.isController()) {
			tankBE.getTankInventory().setFluid(tank.getFluid());
		}
	}

	public static CreativeFluidTankMountedStorage fromTank(FluidTankBlockEntity tankBE) {
		FluidStack fluid = tankBE.getTankInventory().getFluid();
		long capacity = tankBE.getTankInventory().getCapacity();
		return new CreativeFluidTankMountedStorage(fluid.copy(), capacity);
	}
}
