package io.github.fabricators_of_create.porting_lib_ufo.tool.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib_ufo.tool.extensions.VanillaToolActionItem;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin implements VanillaToolActionItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
	}
}
