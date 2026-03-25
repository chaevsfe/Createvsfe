package com.simibubi.create;

/**
 * Registry class for Create's built-in display targets.
 *
 * On Fabric/UfoPort, display source/target registration is handled by
 * {@link com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours#registerDefaults()},
 * which uses the content-layer DisplayTarget hierarchy and AttachedRegistry.
 *
 * The NeoForge version registers into CreateBuiltInRegistries.DISPLAY_TARGET using Registrate,
 * but the UfoPort content-layer targets extend a different class hierarchy. This stub exists
 * for API compatibility — actual registration happens in AllDisplayBehaviours.
 */
public class AllDisplayTargets {

	public static void register() {
		// Registration is handled by AllDisplayBehaviours.registerDefaults()
	}
}
