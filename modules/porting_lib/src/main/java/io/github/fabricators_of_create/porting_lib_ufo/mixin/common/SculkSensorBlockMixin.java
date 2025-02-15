package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SculkSensorBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.block.CustomExpBlock;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin implements CustomExpBlock {
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return silkTouchLevel == 0 ? 5 : 0;
	}
}
