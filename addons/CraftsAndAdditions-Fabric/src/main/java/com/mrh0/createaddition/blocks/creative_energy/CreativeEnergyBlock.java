package com.mrh0.createaddition.blocks.creative_energy;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.mojang.serialization.MapCodec;
public class CreativeEnergyBlock extends Block implements IBE<CreativeEnergyBlockEntity> {
    public static final MapCodec<CreativeEnergyBlock> CODEC = simpleCodec(CreativeEnergyBlock::new);
    @Override protected MapCodec<? extends Block> codec() { return (MapCodec) CODEC; }
    public CreativeEnergyBlock(Properties props) { super(props); }
    @Override public Class<CreativeEnergyBlockEntity> getBlockEntityClass() { return CreativeEnergyBlockEntity.class; }
    @Override public BlockEntityType<? extends CreativeEnergyBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.CREATIVE_ENERGY.get(); }
}
