package com.jozufozu.flywheel.backend.instancing.instancing;

import com.jozufozu.flywheel.core.shader.WorldProgram;

import net.minecraft.client.renderer.RenderType;

/**
 * Compat stub for old Flywheel 0.6.x InstancedMaterialGroup.
 */
public class InstancedMaterialGroup<P extends WorldProgram> {
	public InstancedMaterialGroup(InstancingEngine<P> owner, RenderType type) {
	}

	protected void setup(P program) {
	}

	public void render(Object viewProjection, double camX, double camY, double camZ) {
	}
}
