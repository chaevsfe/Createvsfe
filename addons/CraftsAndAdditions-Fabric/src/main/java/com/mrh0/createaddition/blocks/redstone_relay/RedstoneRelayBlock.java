package com.mrh0.createaddition.blocks.redstone_relay;

import com.mrh0.createaddition.energy.NodeRotation;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import com.mojang.serialization.MapCodec;

public class RedstoneRelayBlock extends Block implements IBE<RedstoneRelayBlockEntity> {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final MapCodec<RedstoneRelayBlock> CODEC = simpleCodec(RedstoneRelayBlock::new);
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }

    public RedstoneRelayBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(VERTICAL, false)
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(NodeRotation.ROTATION, NodeRotation.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL, HORIZONTAL_FACING, POWERED, NodeRotation.ROTATION);
    }

    @Override public Class<RedstoneRelayBlockEntity> getBlockEntityClass() { return RedstoneRelayBlockEntity.class; }
    @Override public BlockEntityType<? extends RedstoneRelayBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.REDSTONE_RELAY.get(); }
}
