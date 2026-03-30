package com.jozufozu.flywheel.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Compat stub for old Flywheel 0.6.x StitchedSprite.
 * Lazily looks up a sprite from the block atlas.
 */
public class StitchedSprite {
	private final ResourceLocation location;
	private TextureAtlasSprite sprite;

	public StitchedSprite(ResourceLocation location) {
		this.location = location;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public ResourceLocation modelLocation() {
		return location;
	}

	public TextureAtlasSprite get() {
		if (sprite == null) {
			sprite = Minecraft.getInstance()
				.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(location);
		}
		return sprite;
	}
}
