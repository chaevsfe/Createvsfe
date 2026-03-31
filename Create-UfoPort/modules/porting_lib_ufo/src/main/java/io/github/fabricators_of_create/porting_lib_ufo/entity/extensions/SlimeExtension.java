package io.github.fabricators_of_create.porting_lib_ufo.entity.extensions;

public interface SlimeExtension {
	/**
	 * Called when the slime spawns particles on landing, see onUpdate.
	 * Return true to prevent the spawning of the default particles.
	 */
	default boolean port_lib_ufo$spawnCustomParticles() { return false; }
}
