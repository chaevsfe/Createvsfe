/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util;

import com.railwayteam.railways.registry.CRItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Contract;

public class ItemUtils {
	@Contract // shut
	public static boolean blocksEndermanView(ItemStack stack, Player wearer, EnderMan enderman) {
		return stack.is(Items.CARVED_PUMPKIN) || stack.is(CRItems.CONDUCTOR_CAPS);
	}

	public static InteractionHand oppositeHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}

	public static void copyStackData(ItemStack source, ItemStack target) {
		CustomData customData = source.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			target.set(DataComponents.CUSTOM_DATA, CustomData.of(customData.copyTag()));
		} else {
			target.remove(DataComponents.CUSTOM_DATA);
		}
	}

	public static boolean isUnbreakable(ItemStack stack) {
		if (stack.isEmpty()) return false;
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		return customData != null && customData.copyTag().getBoolean("Unbreakable");
	}
}
