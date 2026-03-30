package com.mrh0.createaddition.blocks.electric_motor;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.mojang.serialization.MapCodec;
public class ElectricMotorBlock extends DirectionalKineticBlock implements IBE<ElectricMotorBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<ElectricMotorBlock> CODEC = simpleCodec(ElectricMotorBlock::new);
    @Override protected MapCodec<? extends DirectionalKineticBlock> codec() { return (MapCodec) CODEC; }
    public ElectricMotorBlock(Properties props) { super(props); }
    @Override public Axis getRotationAxis(BlockState state) { return state.getValue(FACING).getAxis(); }
    @Override public Class<ElectricMotorBlockEntity> getBlockEntityClass() { return ElectricMotorBlockEntity.class; }
    @Override public BlockEntityType<? extends ElectricMotorBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.ELECTRIC_MOTOR.get(); }
}
