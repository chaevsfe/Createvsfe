package com.mrh0.createaddition.blocks.connector.base;

import com.mrh0.createaddition.energy.NodeRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class AbstractConnectorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final EnumProperty<ConnectorMode> MODE = EnumProperty.create("mode", ConnectorMode.class);
    public static final EnumProperty<ConnectorVariant> VARIANT = EnumProperty.create("variant", ConnectorVariant.class);

    public AbstractConnectorBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODE, ConnectorMode.None)
                .setValue(NodeRotation.ROTATION, NodeRotation.NONE)
                .setValue(VARIANT, ConnectorVariant.Default));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, NodeRotation.ROTATION, VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        Direction dir = c.getClickedFace().getOpposite();
        return this.defaultBlockState().setValue(FACING, dir);
    }
}
