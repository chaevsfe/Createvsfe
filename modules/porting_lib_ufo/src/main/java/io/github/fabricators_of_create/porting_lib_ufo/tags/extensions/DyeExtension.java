package io.github.fabricators_of_create.porting_lib_ufo.tags.extensions;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface DyeExtension {
	default TagKey<Item> port_lib_ufo$getDyesTag() {
		return null;
	}
	
	default TagKey<Item> port_lib_ufo$getDyedTag() {
		return null;
	}
}
