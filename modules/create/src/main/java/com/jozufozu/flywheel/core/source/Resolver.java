package com.jozufozu.flywheel.core.source;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x Resolver.
 */
public class Resolver {
	public static final Resolver INSTANCE = new Resolver();

	public FileResolution get(ResourceLocation location) {
		return new FileResolution();
	}
}
