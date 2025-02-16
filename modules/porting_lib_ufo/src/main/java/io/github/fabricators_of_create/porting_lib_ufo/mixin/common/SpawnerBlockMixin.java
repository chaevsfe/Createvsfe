package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SpawnerBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.block.CustomExpBlock;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin implements CustomExpBlock {

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world,
			net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
		return 15 + randomSource.nextInt(15) + randomSource.nextInt(15);
	}
}
