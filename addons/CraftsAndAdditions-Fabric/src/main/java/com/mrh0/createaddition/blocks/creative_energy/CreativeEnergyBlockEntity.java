package com.mrh0.createaddition.blocks.creative_energy;
import com.mrh0.createaddition.energy.CreativeEnergyStorage;
import com.mrh0.createaddition.energy.fabric.IEnergyStorage;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
public class CreativeEnergyBlockEntity extends SmartBlockEntity {
    private final CreativeEnergyStorage energy = new CreativeEnergyStorage();
    public CreativeEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    public IEnergyStorage getEnergyStorage(Direction side) { return energy; }
    @Override public void tick() { super.tick(); if (level != null && !level.isClientSide()) { for (Direction d : Direction.values()) energy.outputToSide(level, worldPosition, d); } }
}
