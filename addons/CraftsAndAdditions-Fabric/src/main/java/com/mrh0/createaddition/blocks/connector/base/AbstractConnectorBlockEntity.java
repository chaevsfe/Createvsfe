package com.mrh0.createaddition.blocks.connector.base;
import com.mrh0.createaddition.energy.BaseElectricBlockEntity;
import com.mrh0.createaddition.energy.IWireNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public abstract class AbstractConnectorBlockEntity extends BaseElectricBlockEntity implements IWireNode {
    public AbstractConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public int getCapacity() { return 0; }
    @Override public int getMaxIn() { return 0; }
    @Override public int getMaxOut() { return 0; }
    @Override public boolean isEnergyInput(Direction side) { return false; }
    @Override public boolean isEnergyOutput(Direction side) { return false; }
    @Override public int getMaxWireLength() { return 16; }
}
