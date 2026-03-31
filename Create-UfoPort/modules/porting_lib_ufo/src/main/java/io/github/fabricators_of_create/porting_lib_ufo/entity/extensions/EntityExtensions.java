package io.github.fabricators_of_create.porting_lib_ufo.entity.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib_ufo.entity.ITeleporter;

public interface EntityExtensions {
	default CompoundTag port_lib_ufo$getCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> port_lib_ufo$captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> port_lib_ufo$captureDrops(Collection<ItemEntity> value) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * If a rider of this entity can interact with this entity. Should return true on the
	 * ridden entity if so.
	 *
	 * @return if the entity can be interacted with from a rider
	 */
	default boolean port_lib_ufo$canRiderInteract() {
		return false;
	}

	default Entity changeDimension(ServerLevel p_20118_, ITeleporter teleporter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
