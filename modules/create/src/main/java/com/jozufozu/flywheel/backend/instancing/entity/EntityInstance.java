package com.jozufozu.flywheel.backend.instancing.entity;

import org.joml.Vector3f;

import com.jozufozu.flywheel.api.MaterialManager;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Compat stub for old Flywheel 0.6.x EntityInstance.
 */
public abstract class EntityInstance<E extends Entity> {
	protected final MaterialManager materialManager;
	protected final E entity;
	protected final Level world;

	public EntityInstance(MaterialManager materialManager, E entity) {
		this.materialManager = materialManager;
		this.entity = entity;
		this.world = entity.level();
	}

	public void init() {
	}

	public void remove() {
	}

	public void updateLight() {
	}

	public Vector3f getInstancePosition(float partialTicks) {
		return new Vector3f(
			(float) entity.getX(),
			(float) entity.getY(),
			(float) entity.getZ()
		);
	}
}
