package com.simibubi.create;

/**
 * Registry class for Create's built-in display sources.
 *
 * On Fabric/UfoPort, display source/target registration is handled by
 * {@link com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours#registerDefaults()},
 * which uses the content-layer DisplaySource/DisplayTarget hierarchy and AttachedRegistry.
 *
 * The NeoForge version registers into CreateBuiltInRegistries.DISPLAY_SOURCE using Registrate,
 * but the UfoPort content-layer sources extend a different class hierarchy. This stub exists
 * for API compatibility — actual registration happens in AllDisplayBehaviours.
 */
public class AllDisplaySources {

	public static void register() {
		// Registration is handled by AllDisplayBehaviours.registerDefaults()
	}
}
