package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationExtensions {
	default int port_lib_ufo$compareNamespaced(ResourceLocation o) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
