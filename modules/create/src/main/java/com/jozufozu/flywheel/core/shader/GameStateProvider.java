package com.jozufozu.flywheel.core.shader;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x GameStateProvider.
 */
public interface GameStateProvider extends StateProvider {
	default ResourceLocation getID() {
		return null;
	}

	boolean isTrue();

	void alterConstants(ShaderConstants constants);
}
