package com.simibubi.create.compat.trinkets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.goggles.GogglesItem;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Trinkets compatibility for Create.
 * Allows goggles to be worn in the head/face trinket slot and backtank in the chest/back slot.
 * <p>
 * Only loaded when the Trinkets mod is present (checked via {@code Mods.TRINKETS.isLoaded()}).
 */
public class TrinketsCompat {

	/**
	 * Register server-side predicates and suppliers.
	 * Called from {@link com.simibubi.create.Create#onInitialize()} when Trinkets is present.
	 */
	public static void init() {
		// Allow goggles in trinket slots to be detected as "wearing goggles"
		GogglesItem.addIsWearingPredicate(player -> {
			Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
			return component.map(tc -> tc.isEquipped(AllItems.GOGGLES.get())).orElse(false);
		});

		// Allow backtank in trinket slots to supply pressurized air
		BacktankUtil.addBacktankSupplier(TrinketsCompat::getBacktanksFromTrinkets);
	}

	/**
	 * Register client-side trinket renderers.
	 * Called from {@link com.simibubi.create.CreateClient#onInitializeClient()} when Trinkets is present.
	 */
	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		GogglesTrinketRenderer.register();
	}

	/**
	 * Searches all trinket slots on the given entity for items tagged as pressurized air sources (backtanks).
	 */
	private static List<ItemStack> getBacktanksFromTrinkets(LivingEntity entity) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
		return component.map(tc -> {
			List<ItemStack> stacks = new ArrayList<>();
			tc.forEach((slotRef, stack) -> {
				if (!stack.isEmpty() && AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.matches(stack))
					stacks.add(stack);
			});
			return stacks;
		}).orElse(Collections.emptyList());
	}
}
