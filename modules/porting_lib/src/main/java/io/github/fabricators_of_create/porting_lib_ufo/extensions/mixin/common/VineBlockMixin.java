package io.github.fabricators_of_create.porting_lib_ufo.extensions.mixin.common;

import net.minecraft.world.level.block.VineBlock;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.IShearable;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements IShearable {
}
