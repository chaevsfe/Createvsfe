package com.mrh0.createaddition.energy;

import com.mrh0.createaddition.energy.fabric.EnergyLookup;
import com.mrh0.createaddition.energy.fabric.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreativeEnergyStorage implements IEnergyStorage {

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public int getEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    public void outputToSide(Level world, BlockPos pos, Direction side) {
        BlockEntity te = world.getBlockEntity(pos.relative(side));
        if (te == null) return;
        IEnergyStorage ies = EnergyLookup.ENERGY.find(world, pos.relative(side), side.getOpposite());
        if (ies == null) return;
        ies.receiveEnergy(Integer.MAX_VALUE, false);
    }
}
