package com.mrh0.createaddition.blocks.rolling_mill;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;
public class RollingMillBlock extends HorizontalKineticBlock implements IBE<RollingMillBlockEntity> {
    public static final MapCodec<RollingMillBlock> CODEC = simpleCodec(RollingMillBlock::new);
    @Override protected MapCodec<? extends HorizontalKineticBlock> codec() { return (MapCodec) CODEC; }
    public RollingMillBlock(Properties props) { super(props); }
    @Override public Axis getRotationAxis(BlockState state) { return state.getValue(HORIZONTAL_FACING).getClockWise().getAxis(); }
    public Axis getRotationAxis2(BlockState state) { return state.getValue(HORIZONTAL_FACING).getAxis(); }
    @Override public Class<RollingMillBlockEntity> getBlockEntityClass() { return RollingMillBlockEntity.class; }
    @Override public BlockEntityType<? extends RollingMillBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.ROLLING_MILL.get(); }
}
