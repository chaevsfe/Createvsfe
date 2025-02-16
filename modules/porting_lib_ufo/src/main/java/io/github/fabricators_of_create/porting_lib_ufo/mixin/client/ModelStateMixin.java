package io.github.fabricators_of_create.porting_lib_ufo.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.ModelStateExtensions;
import net.minecraft.client.resources.model.ModelState;

@Mixin(ModelState.class)
public interface ModelStateMixin extends ModelStateExtensions {
}
