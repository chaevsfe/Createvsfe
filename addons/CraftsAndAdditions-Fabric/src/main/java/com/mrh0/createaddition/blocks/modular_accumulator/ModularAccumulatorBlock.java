package com.mrh0.createaddition.blocks.modular_accumulator;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import com.mojang.serialization.MapCodec;
public class ModularAccumulatorBlock extends Block implements IBE<ModularAccumulatorBlockEntity> {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final MapCodec<ModularAccumulatorBlock> CODEC = simpleCodec(ModularAccumulatorBlock::new);
    @Override protected MapCodec<? extends Block> codec() { return (MapCodec) CODEC; }
    public ModularAccumulatorBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(TOP, true).setValue(BOTTOM, true));
    }
    public static ModularAccumulatorBlock regular(Properties props) { return new ModularAccumulatorBlock(props); }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM);
    }
    @Override public Class<ModularAccumulatorBlockEntity> getBlockEntityClass() { return ModularAccumulatorBlockEntity.class; }
    @Override public BlockEntityType<? extends ModularAccumulatorBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.MODULAR_ACCUMULATOR.get(); }
}
