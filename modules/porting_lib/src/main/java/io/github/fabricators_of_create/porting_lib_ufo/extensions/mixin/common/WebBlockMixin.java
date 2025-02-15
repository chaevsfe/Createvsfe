package io.github.fabricators_of_create.porting_lib_ufo.extensions.mixin.common;

import net.minecraft.world.level.block.WebBlock;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.IShearable;

@Mixin(WebBlock.class)
public abstract class WebBlockMixin implements IShearable {
}
