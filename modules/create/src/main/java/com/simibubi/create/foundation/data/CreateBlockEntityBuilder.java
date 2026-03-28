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
import com.simibubi.create.Create;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import net.fabricmc.api.EnvType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {

	@Nullable
	private NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory;
	@Nullable
	private Supplier<VisualFactory<T>> visualFactorySupplier;
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

	/**
	 * Register a Flywheel 1.0.6 Visual for this block entity type.
	 * This uses the new VisualizationContext-based API instead of the old MaterialManager pattern.
	 *
	 * The factory is wrapped in a Supplier to prevent eager class loading of client-only Visual
	 * classes on the dedicated server. The Supplier is only resolved on EnvType.CLIENT.
	 */
	/**
	 * Register a Visual with a direct factory reference. The factory is automatically
	 * wrapped in a Supplier to defer class loading on dedicated servers.
	 */
	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory) {
		return visual(() -> factory, true);
	}

	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory, boolean skipVanillaRender) {
		return visual(() -> factory, be -> skipVanillaRender);
	}

	public CreateBlockEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier) {
		return visual(factorySupplier, true);
	}

	public CreateBlockEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier, boolean skipVanillaRender) {
		return visual(factorySupplier, be -> skipVanillaRender);
	}

	public CreateBlockEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier, Predicate<T> skipVanillaRender) {
		if (this.visualFactorySupplier == null) {
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerVisual);
		}
		this.visualFactorySupplier = factorySupplier;
		this.renderNormally = be -> !skipVanillaRender.test(be);
		return this;
	}

	protected void registerVisual() {
		onRegister(entry -> {
			VisualFactory<T> factory = visualFactorySupplier.get();
			SimpleBlockEntityVisualizer.builder(entry)
				.factory((ctx, be, pt) -> factory.create(ctx, be, pt))
				.skipVanillaRender(be -> renderNormally != null && !renderNormally.test(be))
				.apply();
		});
	}

	/**
	 * Factory interface for creating Flywheel 1.0.6 Visual instances.
	 */
	@FunctionalInterface
	public interface VisualFactory<T extends BlockEntity> {
		dev.engine_room.flywheel.api.visual.BlockEntityVisual<? super T> create(VisualizationContext ctx, T blockEntity, float partialTick);
	}
}
