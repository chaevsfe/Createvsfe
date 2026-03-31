package io.github.fabricators_of_create.porting_lib_ufo.entity.extensions;

import net.minecraft.core.BlockPos;

public interface AbstractMinecartExtensions {
	default void port_lib_ufo$moveMinecartOnRail(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default BlockPos port_lib_ufo$getCurrentRailPos() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default float port_lib_ufo$getMaxSpeedOnRail() {
		return 1.2f; // default in Forge
	}
}
