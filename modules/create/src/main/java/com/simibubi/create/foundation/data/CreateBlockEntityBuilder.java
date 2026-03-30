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

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import net.fabricmc.api.EnvType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {

	@Nullable
	private NonNullSupplier<BiFunction<MaterialManager, T, BlockEntityInstance<? super T>>> instanceFactory;
	@Nullable
	private Supplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactorySupplier;
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
	 * Register a Visual with a direct factory reference (no Supplier wrapper needed).
	 * Use the Supplier overloads when passing lambdas that reference client-only classes
	 * to defer class loading on dedicated servers.
	 */
	public CreateBlockEntityBuilder<T, P> visual(SimpleBlockEntityVisualizer.Factory<T> factory) {
		return visual(() -> factory, true);
	}

	public CreateBlockEntityBuilder<T, P> visual(SimpleBlockEntityVisualizer.Factory<T> factory, boolean renderNormally) {
		return visual(() -> factory, be -> renderNormally);
	}

	public CreateBlockEntityBuilder<T, P> visual(Supplier<SimpleBlockEntityVisualizer.Factory<T>> factorySupplier) {
		return visual(factorySupplier, true);
	}

	public CreateBlockEntityBuilder<T, P> visual(Supplier<SimpleBlockEntityVisualizer.Factory<T>> factorySupplier, boolean renderNormally) {
		return visual(factorySupplier, be -> renderNormally);
	}

	/**
	 * Register a Visual with a renderNormally predicate.
	 * When renderNormally returns true, the vanilla BER will ALSO run alongside the Visual.
	 * When renderNormally returns false, only the Visual runs (BER is skipped).
	 * This matches NeoForge's convention where the predicate parameter means "render normally"
	 * (i.e., run the BER), NOT "skip vanilla render".
	 */
	public CreateBlockEntityBuilder<T, P> visual(Supplier<SimpleBlockEntityVisualizer.Factory<T>> factorySupplier, Predicate<T> renderNormally) {
		if (this.visualFactorySupplier == null) {
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerVisual);
		}
		this.visualFactorySupplier = factorySupplier;
		this.renderNormally = renderNormally;
		return this;
	}

	protected void registerVisual() {
		onRegister(entry -> {
			SimpleBlockEntityVisualizer.Factory<T> factory = visualFactorySupplier.get();
			SimpleBlockEntityVisualizer.builder(entry)
				.factory(factory)
				.skipVanillaRender(be -> renderNormally != null && !renderNormally.test(be))
				.apply();
		});
	}

	/**
	 * Kept for binary compatibility with addon jars compiled against the old API.
	 * Addons (SNR, CreateConnected) reference this type in their compiled bytecode.
	 */
	@FunctionalInterface
	public interface VisualFactory<T extends BlockEntity> extends SimpleBlockEntityVisualizer.Factory<T> {}

	// Bridge methods for addon binary compat (exact method signatures addons call)
	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory) {
		return visual((SimpleBlockEntityVisualizer.Factory<T>) factory);
	}

	public CreateBlockEntityBuilder<T, P> visual(VisualFactory<T> factory, boolean renderNormally) {
		return visual((SimpleBlockEntityVisualizer.Factory<T>) factory, renderNormally);
	}
}
