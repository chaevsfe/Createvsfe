package com.mrh0.createaddition.blocks.liquid_blaze_burner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import com.mojang.serialization.MapCodec;
public class LiquidBlazeBurnerBlock extends HorizontalDirectionalBlock implements IBE<LiquidBlazeBurnerBlockEntity> {
    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = BlazeBurnerBlock.HEAT_LEVEL;
    public static final MapCodec<LiquidBlazeBurnerBlock> CODEC = simpleCodec(LiquidBlazeBurnerBlock::new);
    @Override protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return (MapCodec) CODEC; }
    public LiquidBlazeBurnerBlock(Properties props) { super(props); }
    @Override public Class<LiquidBlazeBurnerBlockEntity> getBlockEntityClass() { return LiquidBlazeBurnerBlockEntity.class; }
    @Override public BlockEntityType<? extends LiquidBlazeBurnerBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.LIQUID_BLAZE_BURNER.get(); }
    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, ItemStack stack, boolean b1, boolean b2, boolean simulate) {
        return InteractionResultHolder.pass(stack);
    }
}
