package com.jozufozu.flywheel.api;

import java.util.function.Supplier;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compat stub for old Flywheel 0.6.x Material.
 */
public class Material<D> {
	@SuppressWarnings("unchecked")
	public Instancer<D> model(Object key, Supplier<?> modelSupplier) {
		return (Instancer<D>) new Instancer<>();
	}

	@SuppressWarnings("unchecked")
	public Instancer<D> getModel(PartialModel model) {
		return (Instancer<D>) new Instancer<>();
	}

	@SuppressWarnings("unchecked")
	public Instancer<D> getModel(PartialModel model, BlockState state) {
		return (Instancer<D>) new Instancer<>();
	}

	@SuppressWarnings("unchecked")
	public Instancer<D> getModel(PartialModel model, BlockState state, Direction dir) {
		return (Instancer<D>) new Instancer<>();
	}

	@SuppressWarnings("unchecked")
	public Instancer<D> getModel(PartialModel model, BlockState state, Direction dir, Supplier<PoseStack> transform) {
		return (Instancer<D>) new Instancer<>();
	}

	@SuppressWarnings("unchecked")
	public Instancer<D> getModel(BlockState state) {
		return (Instancer<D>) new Instancer<>();
	}
}
