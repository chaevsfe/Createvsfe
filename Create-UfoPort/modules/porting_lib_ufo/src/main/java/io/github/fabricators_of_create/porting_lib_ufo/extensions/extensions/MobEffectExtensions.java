package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public interface MobEffectExtensions {
	private MobEffect self() {
		return (MobEffect)this;
	}

	/**
	 * Get a fresh list of items that can cure this Potion.
	 * All new PotionEffects created from this Potion will call this to initialize the default curative items
	 * @see MobEffectInstanceExtensions#getCurativeItems()
	 * @return A list of items that can cure this Potion
	 */
	default List<ItemStack> port_lib_ufo$getCurativeItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(Items.MILK_BUCKET));
		return ret;
	}

	/**
	 * Used for determining {@code PotionEffect} sort order in GUIs.
	 * Defaults to the {@code PotionEffect}'s liquid color.
	 * @param potionEffect the {@code PotionEffect} instance containing the potion
	 * @return a value used to sort {@code PotionEffect}s in GUIs
	 */
	default int port_lib_ufo$getSortOrder(MobEffectInstance potionEffect) {
		return self().getColor();
	} // TODO: Implement
}
