package io.github.fabricators_of_create.porting_lib_ufo.tool.mixin;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.tool.extensions.BlockStateExtensions;

@Mixin(BlockState.class)
public class BlockStateMixin implements BlockStateExtensions {
}
