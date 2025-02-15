package io.github.fabricators_of_create.porting_lib_ufo.tool.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib_ufo.tool.extensions.VanillaToolActionItem;

@Mixin(ShovelItem.class)
public class ShovelItemMixin implements VanillaToolActionItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}
}
