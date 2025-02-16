package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.world.level.saveddata.maps.MapDecoration;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.MapDecorationExtensions;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin implements MapDecorationExtensions {
}
