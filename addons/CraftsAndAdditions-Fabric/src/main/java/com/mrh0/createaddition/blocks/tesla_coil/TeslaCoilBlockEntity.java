package com.mrh0.createaddition.blocks.tesla_coil;
import com.mrh0.createaddition.energy.BaseElectricBlockEntity;
import com.mrh0.createaddition.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public class TeslaCoilBlockEntity extends BaseElectricBlockEntity {
    public TeslaCoilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public int getCapacity() { return Config.TESLA_COIL_CAPACITY; }
    @Override public int getMaxIn() { return Config.TESLA_COIL_MAX_INPUT; }
    @Override public int getMaxOut() { return 0; }
    @Override public boolean isEnergyInput(Direction side) { return true; }
    @Override public boolean isEnergyOutput(Direction side) { return false; }
}
