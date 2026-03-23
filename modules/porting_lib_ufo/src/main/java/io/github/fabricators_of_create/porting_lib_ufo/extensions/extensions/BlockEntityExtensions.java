package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityExtensions {
	default CompoundTag getCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void deserializeNBT(BlockState state, CompoundTag nbt) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * Called when this is first added to the world or right before the first tick
	 * when the chunk is generated or loaded from disk.
	 *
	 * Prefixed with port_lib_ufo$ to avoid IncompatibleClassChangeError when the
	 * standard Porting Lib (porting_lib_extensions) is also loaded, since both
	 * inject interfaces into BlockEntity with default onLoad() methods.
	 * See: https://github.com/vlad250906/Create-UfoPort/issues/17
	 */
	default void port_lib_ufo$onLoad() {
	}

	default void invalidateCaps() {
	}
}
