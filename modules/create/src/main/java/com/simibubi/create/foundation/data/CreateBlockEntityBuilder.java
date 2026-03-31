package com.simibubi.create.foundation.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.fabricmc.api.EnvType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {

	@Nullable
	private NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory;
	@Nullable
	private Supplier<?> visualFactorySupplier; // Supplier<Factory<T>> erased to avoid server classloading
	private Predicate<T> renderNormally;

	private Collection<NonNullSupplier<? extends Collection<NonNullSupplier<? extends Block>>>> deferredValidBlocks =
		new ArrayList<>();

	public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent,
		String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
		return new CreateBlockEntityBuilder<>(owner, parent, name, callback, factory);
	}

	protected CreateBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback,
		BlockEntityFactory<T> factory) {
		super(owner, parent, name, callback, factory);
	}

	public CreateBlockEntityBuilder<T, P> validBlocksDeferred(
		NonNullSupplier<? extends Collection<NonNullSupplier<? extends Block>>> blocks) {
		deferredValidBlocks.add(blocks);
		return this;
	}

	@Override
	protected BlockEntityType<T> createEntry() {
		deferredValidBlocks.stream()
			.map(Supplier::get)
			.flatMap(Collection::stream)
			.forEach(this::validBlock);
		return super.createEntry();
	}

	public CreateBlockEntityBuilder<T, P> instance(
		NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory) {
		return instance(instanceFactory, true);
	}

	public CreateBlockEntityBuilder<T, P> instance(
		NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory,
		boolean renderNormally) {
		return instance(instanceFactory, be -> renderNormally);
	}

	public CreateBlockEntityBuilder<T, P> instance(
		NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory,
		Predicate<T> renderNormally) {
		if (this.instanceFactory == null) {
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerInstance);
		}

		this.instanceFactory = instanceFactory;
		this.renderNormally = renderNormally;

		return this;
	}

	protected void registerInstance() {
		onRegister(entry ->
				InstancedRenderRegistry.configure(entry)
						.factory(instanceFactory.get())
						.skipRender(be -> !renderNormally.test(be))
						.apply()
		);
	}

	// ---- Flywheel 1.0.6 Visual API ----
	// visual() accepts Supplier<?> to avoid loading Flywheel classes on dedicated servers.
	// The actual Factory type is resolved only inside registerVisual() on client.

	public <F> CreateBlockEntityBuilder<T, P> visual(Supplier<F> factorySupplier) {
		return visual(factorySupplier, true);
	}

	public <F> CreateBlockEntityBuilder<T, P> visual(Supplier<F> factorySupplier, boolean renderNormally) {
		return visual(factorySupplier, be -> renderNormally);
	}

	public <F> CreateBlockEntityBuilder<T, P> visual(Supplier<F> factorySupplier, Predicate<T> renderNormally) {
		if (this.visualFactorySupplier == null) {
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerVisual);
		}
		this.visualFactorySupplier = factorySupplier;
		this.renderNormally = renderNormally;
		return this;
	}

	@SuppressWarnings("unchecked")
	protected void registerVisual() {
		onRegister(entry -> {
			// Safe to reference Flywheel types here — this method only runs on client via EnvExecutor
			dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.Factory<T> factory =
				(dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.Factory<T>) visualFactorySupplier.get();
			dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.builder(entry)
				.factory(factory)
				.skipVanillaRender(be -> renderNormally != null && !renderNormally.test(be))
				.apply();
		});
	}

	/**
	 * Kept for binary compatibility with addon jars compiled against the old API.
	 * Addons (SNR, CreateConnected) reference this type in their compiled bytecode.
	 * This interface is only loaded on CLIENT (where Flywheel is available).
	 */
	@FunctionalInterface
	public interface VisualFactory<T extends BlockEntity> extends dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.Factory<T> {}

	// Bridge methods for addon binary compat (exact method signatures addon bytecode calls)
	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory) {
		return visual((Supplier<VisualFactory<T>>) () -> factory);
	}

	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory, boolean renderNormally) {
		return visual((Supplier<VisualFactory<T>>) () -> factory, renderNormally);
	}
}
