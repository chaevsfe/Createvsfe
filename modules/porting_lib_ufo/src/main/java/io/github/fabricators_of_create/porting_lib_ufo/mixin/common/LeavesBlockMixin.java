package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.IShearable;
import net.minecraft.world.level.block.LeavesBlock;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin implements IShearable {
}
