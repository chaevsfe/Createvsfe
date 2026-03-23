package com.jozufozu.flywheel.event;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;

/**
 * Compat stub for old Flywheel 0.6.x BeginFrameEvent.
 */
public class BeginFrameEvent {
	public Frustum getFrustum() {
		return null;
	}

	public Camera getCamera() {
		return null;
	}

	public Vec3 getCameraPos() {
		return Vec3.ZERO;
	}

	public net.minecraft.world.level.LevelAccessor getWorld() {
		return null;
	}
}
