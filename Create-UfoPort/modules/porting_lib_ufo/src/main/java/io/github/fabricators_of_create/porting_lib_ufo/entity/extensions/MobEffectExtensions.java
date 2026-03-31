package io.github.fabricators_of_create.porting_lib_ufo.entity.extensions;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib_ufo.entity.client.MobEffectRenderer;

public interface MobEffectExtensions {
	@Nullable
	default MobEffectRenderer port_lib_ufo$getRenderer() {
		return null;
	}
}
