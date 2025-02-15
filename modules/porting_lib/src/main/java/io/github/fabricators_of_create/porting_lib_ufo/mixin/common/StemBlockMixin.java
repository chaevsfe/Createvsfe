package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StemBlock;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib_ufo.common.util.PlantType;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin implements IPlantable {
	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
}
