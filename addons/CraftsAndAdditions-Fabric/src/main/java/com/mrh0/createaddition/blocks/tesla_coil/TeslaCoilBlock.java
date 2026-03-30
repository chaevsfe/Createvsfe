package com.mrh0.createaddition.blocks.tesla_coil;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import com.mrh0.createaddition.shapes.CAShapes;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import com.mojang.serialization.MapCodec;
public class TeslaCoilBlock extends DirectionalBlock implements IBE<TeslaCoilBlockEntity>, IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<TeslaCoilBlock> CODEC = simpleCodec(TeslaCoilBlock::new);
    @Override protected MapCodec<? extends DirectionalBlock> codec() { return (MapCodec) CODEC; }
    public TeslaCoilBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(POWERED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
    @Override public Class<TeslaCoilBlockEntity> getBlockEntityClass() { return TeslaCoilBlockEntity.class; }
    @Override public BlockEntityType<? extends TeslaCoilBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.TESLA_COIL.get(); }
}
