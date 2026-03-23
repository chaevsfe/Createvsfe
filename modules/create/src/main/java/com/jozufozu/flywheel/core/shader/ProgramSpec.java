package com.jozufozu.flywheel.core.shader;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x ProgramSpec.
 */
public class ProgramSpec {
	private final ResourceLocation name;

	public ProgramSpec(ResourceLocation name) {
		this.name = name;
	}

	public ResourceLocation getName() {
		return name;
	}
}
