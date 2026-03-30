package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SlotExtensions {
	default Slot port_lib_ufo$setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default int port_lib_ufo$getSlotIndex() {
		return 0;
	}
}
