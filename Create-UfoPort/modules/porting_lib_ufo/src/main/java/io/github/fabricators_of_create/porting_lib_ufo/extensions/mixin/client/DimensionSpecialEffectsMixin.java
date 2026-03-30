package io.github.fabricators_of_create.porting_lib_ufo.extensions.mixin.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.DimensionSpecialEffectsExtensions;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin implements DimensionSpecialEffectsExtensions {
}
