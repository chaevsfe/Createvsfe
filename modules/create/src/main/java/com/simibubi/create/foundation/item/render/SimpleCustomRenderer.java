package com.simibubi.create.foundation.item.render;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.item.Item;

/**
 * Fabric equivalent of NeoForge's IClientItemExtensions-based SimpleCustomRenderer.
 * On Fabric, custom item rendering is registered via BuiltinItemRendererRegistry + CustomRenderedItems.register().
 * This class mirrors the NeoForge API surface so items can call SimpleCustomRenderer.create(this, renderer)
 * in initializeClient() and have the renderer and item registered correctly.
 */
public class SimpleCustomRenderer {

	protected final CustomRenderedItemModelRenderer renderer;

	protected SimpleCustomRenderer(CustomRenderedItemModelRenderer renderer) {
		this.renderer = renderer;
	}

	public static SimpleCustomRenderer create(Item item, CustomRenderedItemModelRenderer renderer) {
		BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
		CustomRenderedItems.register(item);
		return new SimpleCustomRenderer(renderer);
	}

	public CustomRenderedItemModelRenderer getCustomRenderer() {
		return renderer;
	}

}
