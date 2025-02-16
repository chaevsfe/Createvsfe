package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.world.level.LevelReader;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.LevelReaderExtensions;

@Mixin(LevelReader.class)
public interface LevelReaderMixin extends LevelReaderExtensions {
}
