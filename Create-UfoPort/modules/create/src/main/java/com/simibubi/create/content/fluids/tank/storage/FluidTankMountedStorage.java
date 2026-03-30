package com.simibubi.create.content.fluids.tank.storage;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mounted fluid storage for fluid tanks.
 * Stores the tank's current fluid and capacity for transport on contraptions.
 */
public class FluidTankMountedStorage extends MountedFluidStorage {
	public static final MapCodec<FluidTankMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		FluidStack.CODEC.optionalFieldOf("Fluid", FluidStack.EMPTY).forGetter(s -> s.fluidStack),
		Codec.LONG.optionalFieldOf("Capacity", 0L).forGetter(s -> s.capacity)
	).apply(i, FluidTankMountedStorage::new));

	protected FluidStack fluidStack;
	protected final long capacity;
	protected SmartFluidTank tank;

	protected FluidTankMountedStorage(FluidStack fluidStack, long capacity) {
		this(AllMountedStorageTypes.FLUID_TANK.get(), fluidStack, capacity);
	}

	protected FluidTankMountedStorage(MountedFluidStorageType<?> type, FluidStack fluidStack, long capacity) {
		super(type);
		this.fluidStack = fluidStack;
		this.capacity = capacity;
		this.tank = new SmartFluidTank(capacity, fs -> {});
		this.tank.setFluid(fluidStack);
	}

	@Override
	public Storage<FluidVariant> getStorage() {
		return tank;
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (be instanceof FluidTankBlockEntity tankBE && tankBE.isController()) {
			FluidStack current = tank.getFluid();
			tankBE.getTankInventory().setFluid(current);
		}
	}

	public static FluidTankMountedStorage fromTank(FluidTankBlockEntity tankBE) {
		FluidStack fluid = tankBE.getTankInventory().getFluid();
		long capacity = tankBE.getTankInventory().getCapacity();
		return new FluidTankMountedStorage(fluid.copy(), capacity);
	}
}
