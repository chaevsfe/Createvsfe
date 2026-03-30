package com.simibubi.create.foundation.mixin.compat.xaeros;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import xaero.map.MapProcessor;

// Use targets string form to avoid loading GuiMap's class hierarchy at compile time.
// GuiMap extends ScreenBase which extends xaero.lib.client.gui.ScreenBase (separate jar),
// causing "cannot access ScreenBase" compile errors without the xaerolib transitive dep.
@Mixin(targets = "xaero.map.gui.GuiMap", remap = false)
public interface XaeroFullscreenMapAccessor {
	@Accessor("cameraX")
	double create$getCameraX();

	@Accessor("cameraZ")
	double create$getCameraZ();

	@Accessor("scale")
	double create$getScale();

	@Accessor("mapProcessor")
	MapProcessor create$getMapProcessor();
}
