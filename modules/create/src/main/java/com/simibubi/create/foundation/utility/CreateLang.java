package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.Create;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create-namespaced lang builder. Delegates to {@link Lang} using Create.ID as the namespace.
 * Provided as a compatibility shim so code ported from NeoForge that uses CreateLang compiles unchanged.
 */
public class CreateLang {

	public static MutableComponent translateDirect(String key, Object... args) {
		return Lang.translateDirect(key, args);
	}

	public static List<Component> translatedOptions(String prefix, String... keys) {
		List<Component> result = new ArrayList<>(keys.length);
		for (String key : keys)
			result.add(translate((prefix != null ? prefix + "." : "") + key).component());
		return result;
	}

	public static LangBuilder builder() {
		return new LangBuilder(Create.ID);
	}

	public static LangBuilder blockName(BlockState state) {
		return builder().add(state.getBlock().getName());
	}

	public static LangBuilder itemName(ItemStack stack) {
		return builder().add(stack.getHoverName().copy());
	}

	public static LangBuilder number(double d) {
		return Lang.number(d);
	}

	public static LangBuilder translate(String langKey, Object... args) {
		return Lang.translate(langKey, args);
	}

	public static LangBuilder text(String text) {
		return Lang.text(text);
	}

}
