package com.simibubi.create.foundation.render.compat;

import net.minecraft.resources.ResourceLocation;

/**
 * Utility for ResourceLocation path manipulation.
 */
public class ResourceUtil {
	public static ResourceLocation subPath(ResourceLocation loc, String suffix) {
		return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), loc.getPath() + suffix);
	}
}
