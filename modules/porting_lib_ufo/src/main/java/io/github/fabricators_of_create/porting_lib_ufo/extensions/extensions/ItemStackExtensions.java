package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemStackExtensions {


	/**
	 * Called before a block is broken. Return true to prevent default block
	 * harvesting.
	 *
	 * Note: In SMP, this is called on both client and server sides!
	 *
	 * @param pos       Block's position in world
	 * @param player    The Player that is wielding the item
	 * @return True to prevent harvesting, false to continue as normal
	 */
	default boolean port_lib_ufo$onBlockStartBreak(BlockPos pos, Player player) {
		return !((ItemStack) this).isEmpty() && ((ItemStack) this).getItem().port_lib_ufo$onBlockStartBreak(((ItemStack) this), pos, player);
	}
}
