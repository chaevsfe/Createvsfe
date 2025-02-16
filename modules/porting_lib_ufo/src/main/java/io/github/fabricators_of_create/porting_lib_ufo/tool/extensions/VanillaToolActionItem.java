package io.github.fabricators_of_create.porting_lib_ufo.tool.extensions;

import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolAction;
import net.minecraft.world.item.ItemStack;

public interface VanillaToolActionItem {
	boolean port_lib$canPerformAction(ItemStack stack, ToolAction toolAction);
}
