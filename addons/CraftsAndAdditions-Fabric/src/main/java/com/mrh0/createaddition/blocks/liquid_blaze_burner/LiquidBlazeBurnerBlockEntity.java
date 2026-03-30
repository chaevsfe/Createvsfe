package com.mrh0.createaddition.blocks.liquid_blaze_burner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
public class LiquidBlazeBurnerBlockEntity extends SmartBlockEntity {
    public LiquidBlazeBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() { return getBlockState().getValue(LiquidBlazeBurnerBlock.HEAT_LEVEL); }
}
