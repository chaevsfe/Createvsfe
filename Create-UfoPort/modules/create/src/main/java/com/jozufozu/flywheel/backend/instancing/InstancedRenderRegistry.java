package com.jozufozu.flywheel.backend.instancing;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Compat stub for old Flywheel 0.6.x InstancedRenderRegistry.
 * All registrations are no-ops; instancing is disabled.
 */
public class InstancedRenderRegistry {

	public static boolean shouldSkipRender(BlockEntity blockEntity) {
		return false; // Never skip: always use BER rendering
	}

	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> BlockEntityConfig<T> configure(BlockEntityType<T> type) {
		return new BlockEntityConfig<>();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> EntityConfig<T> configure(EntityType<T> type) {
		return new EntityConfig<>();
	}

	public static class BlockEntityConfig<T extends BlockEntity> {
		public BlockEntityConfig<T> factory(BiFunction<MaterialManager, T, BlockEntityInstance<? super T>> factory) {
			return this;
		}

		public BlockEntityConfig<T> skipRender(Predicate<T> skipRender) {
			return this;
		}

		public void apply() {
			// no-op
		}
	}

	public static class EntityConfig<T extends Entity> {
		public EntityConfig<T> factory(BiFunction<MaterialManager, T, EntityInstance<? super T>> factory) {
			return this;
		}

		public EntityConfig<T> skipRender(Predicate<T> skipRender) {
			return this;
		}

		public void apply() {
			// no-op
		}
	}
}
