package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SugarCaneBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib_ufo.common.util.PlantType;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block implements IPlantable {
	public SugarCaneBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.BEACH;
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		return defaultBlockState();
	}
}
