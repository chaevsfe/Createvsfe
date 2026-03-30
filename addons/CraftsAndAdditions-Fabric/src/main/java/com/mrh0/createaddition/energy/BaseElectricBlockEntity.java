package com.mrh0.createaddition.energy;

import com.mrh0.createaddition.energy.fabric.EnergyLookup;
import com.mrh0.createaddition.energy.fabric.IEnergyStorage;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public abstract class BaseElectricBlockEntity extends SmartBlockEntity {

    protected final InternalEnergyStorage localEnergy;

    private EnumSet<Direction> invalidSides = EnumSet.allOf(Direction.class);
    private EnumMap<Direction, IEnergyStorage> escacheMap = new EnumMap<>(Direction.class);

    public BaseElectricBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        localEnergy = new InternalEnergyStorage(getCapacity(), getMaxIn(), getMaxOut());
        setLazyTickRate(20);
    }

    public abstract int getCapacity();
    public abstract int getMaxIn();
    public abstract int getMaxOut();

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public IEnergyStorage getEnergyStorage(Direction side) {
        if (isEnergyInput(side) || isEnergyOutput(side))
            return localEnergy;
        return null;
    }

    public abstract boolean isEnergyInput(Direction side);
    public abstract boolean isEnergyOutput(Direction side);

    @Override
    protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider, boolean arg1) {
        super.read(compound, provider, arg1);
        localEnergy.read(compound);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        localEnergy.write(compound);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Deprecated
    public void outputTick(int max) {
        for (Direction side : Direction.values()) {
            if (!isEnergyOutput(side))
                continue;
            localEnergy.outputToSide(level, worldPosition, side, max);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!invalidSides.isEmpty()) {
            invalidSides.forEach(this::updateCache);
            invalidSides.clear();
        }
    }

    public boolean ignoreCapSide() {
        return false;
    }

    private void invalidCache(Direction side) {
        invalidSides.add(side);
    }

    public void updateCache() {
        if (level.isClientSide())
            return;
        for (Direction side : Direction.values()) {
            updateCache(side);
        }
    }

    public void updateCache(Direction side) {
        if (!level.isLoaded(worldPosition.relative(side))) {
            setCache(side, null);
            return;
        }
        BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
        if (te == null) {
            setCache(side, null);
            return;
        }
        IEnergyStorage es = EnergyLookup.ENERGY.find(level, worldPosition.relative(side), side.getOpposite());
        if (ignoreCapSide() && es == null) {
            es = EnergyLookup.ENERGY.find(level, worldPosition.relative(side), null);
        }
        setCache(side, es);
    }

    public void setCache(Direction side, IEnergyStorage storage) {
        escacheMap.put(side, storage);
    }

    public IEnergyStorage getCachedEnergy(Direction side) {
        return escacheMap.get(side);
    }
}
