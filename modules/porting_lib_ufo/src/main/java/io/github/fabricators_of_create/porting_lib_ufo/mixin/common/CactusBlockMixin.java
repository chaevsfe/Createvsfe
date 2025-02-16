package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib_ufo.common.util.PlantType;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block implements IPlantable {
	public CactusBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.DESERT;
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		return defaultBlockState();
	}
}
