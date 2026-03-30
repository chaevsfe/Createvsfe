package com.mrh0.createaddition.blocks.connector;
import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public class SmallConnectorBlockEntity extends AbstractConnectorBlockEntity {
    public SmallConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
}
