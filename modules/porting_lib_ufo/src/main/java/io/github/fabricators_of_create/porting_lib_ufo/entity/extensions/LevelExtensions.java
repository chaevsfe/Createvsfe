package io.github.fabricators_of_create.porting_lib_ufo.entity.extensions;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib_ufo.entity.PartEntity;

public interface LevelExtensions {
	/**
	 * All part entities in this world. Used when collecting entities in an AABB to fix parts being
	 * ignored whose parent entity is in a chunk that does not intersect with the AABB.
	 */
	default Collection<PartEntity<?>> port_lib_ufo$getPartEntities() {
		return port_lib_ufo$getPartEntityMap().values();
	}

	default Int2ObjectMap<PartEntity<?>> port_lib_ufo$getPartEntityMap() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
