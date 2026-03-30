package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.world.level.block.state.BlockState;

public interface CameraExtensions {
	default void port_lib_ufo$setAnglesInternal(float yaw, float pitch) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default BlockState port_lib_ufo$getBlockAtCamera() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
