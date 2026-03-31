package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public interface IShearable {
	default boolean port_lib_ufo$isShearable(ItemStack item, Level world, BlockPos pos) {
		return false;
	}

	default List<ItemStack> port_lib_ufo$onSheared(@Nullable Player player, ItemStack item, Level world, BlockPos pos, int fortune) {
		return Collections.emptyList();
	}
}
