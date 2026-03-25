package com.simibubi.create;

/**
 * Top-level registry class for contraption types.
 *
 * On Fabric/UfoPort, contraption type registration is handled directly by
 * {@link com.simibubi.create.content.contraptions.ContraptionType} which uses a simple
 * string-to-factory map. Types are registered as static fields in that class.
 *
 * The NeoForge version uses CreateBuiltInRegistries.CONTRAPTION_TYPE with Holder references.
 * This stub exists for API compatibility — actual registration happens in ContraptionType.
 */
public class AllContraptionTypes {

	public static void init() {
		// Triggers class loading of content.contraptions.ContraptionType which registers all types.
		com.simibubi.create.content.contraptions.ContraptionType.ENTRIES.size();
	}
}
