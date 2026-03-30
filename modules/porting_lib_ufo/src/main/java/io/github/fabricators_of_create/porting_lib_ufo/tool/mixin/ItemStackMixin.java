package io.github.fabricators_of_create.porting_lib_ufo.tool.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib_ufo.tool.addons.ToolActionItem;
import io.github.fabricators_of_create.porting_lib_ufo.tool.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib_ufo.tool.extensions.VanillaToolActionItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackExtensions {
	@Shadow
	public abstract Item getItem();

	@Override
	public boolean port_lib_ufo$canPerformAction(ToolAction toolAction) {
		var item = getItem();
		if (item instanceof ToolActionItem toolActionItem)
			return toolActionItem.canPerformAction((ItemStack) (Object) this, toolAction);
		if (item instanceof VanillaToolActionItem toolActionItem)
			return toolActionItem.port_lib$canPerformAction((ItemStack) (Object) this, toolAction);
		return false;
	}
}
