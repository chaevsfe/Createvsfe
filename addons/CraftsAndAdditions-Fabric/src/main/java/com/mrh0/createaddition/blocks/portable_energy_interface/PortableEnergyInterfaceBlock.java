package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class PortableEnergyInterfaceBlock extends WrenchableDirectionalBlock implements IBE<PortableEnergyInterfaceBlockEntity> {
    public static final MapCodec<PortableEnergyInterfaceBlock> CODEC = simpleCodec(PortableEnergyInterfaceBlock::new);
    @Override protected MapCodec<? extends WrenchableDirectionalBlock> codec() { return (MapCodec) CODEC; }

    public PortableEnergyInterfaceBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer().isSteppingCarefully()) {
            direction = direction.getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override public Class<PortableEnergyInterfaceBlockEntity> getBlockEntityClass() { return PortableEnergyInterfaceBlockEntity.class; }
    @Override public BlockEntityType<? extends PortableEnergyInterfaceBlockEntity> getBlockEntityType() { return com.mrh0.createaddition.index.CABlockEntities.PORTABLE_ENERGY_INTERFACE.get(); }
}
