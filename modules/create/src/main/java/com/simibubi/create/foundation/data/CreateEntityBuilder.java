package com.simibubi.create.foundation.data;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.simibubi.create.Create;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@ParametersAreNonnullByDefault
public class CreateEntityBuilder<T extends Entity, P> extends EntityBuilder<T, P> {

	@Nullable
	private NonNullSupplier<BiFunction<MaterialManager, T, EntityInstance<? super T>>> instanceFactory;
	@Nullable
	private Supplier<VisualFactory<T>> visualFactorySupplier;
	private Predicate<T> renderNormally;

	public static <T extends Entity, P> EntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory classification) {
		return (new CreateEntityBuilder<>(owner, parent, name, callback, factory, classification)).defaultLang();
	}

	public CreateEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory classification) {
		super(owner, parent, name, callback, factory, classification/*, (mobCategory, tEntityFactory) -> FabricEntityTypeBuilder.create(mobCategory, tEntityFactory)*/);
	}

	public CreateEntityBuilder<T, P> instance(NonNullSupplier<BiFunction<MaterialManager, T, EntityInstance<? super T>>> instanceFactory) {
		return instance(instanceFactory, true);
	}

	public CreateEntityBuilder<T, P> instance(NonNullSupplier<BiFunction<MaterialManager, T, EntityInstance<? super T>>> instanceFactory, boolean renderNormally) {
		return instance(instanceFactory, be -> renderNormally);
	}

	public CreateEntityBuilder<T, P> instance(NonNullSupplier<BiFunction<MaterialManager, T, EntityInstance<? super T>>> instanceFactory, Predicate<T> renderNormally) {
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

	public CreateEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier) {
		return visual(factorySupplier, true);
	}

	public CreateEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier, boolean skipVanillaRender) {
		return visual(factorySupplier, e -> skipVanillaRender);
	}

	public CreateEntityBuilder<T, P> visual(Supplier<VisualFactory<T>> factorySupplier, Predicate<T> skipVanillaRender) {
		if (this.visualFactorySupplier == null) {
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::registerVisual);
		}
		this.visualFactorySupplier = factorySupplier;
		this.renderNormally = e -> !skipVanillaRender.test(e);
		return this;
	}

	protected void registerVisual() {
		onRegister(entry -> {
			VisualFactory<T> factory = visualFactorySupplier.get();
			SimpleEntityVisualizer.builder(entry)
				.factory((ctx, entity, pt) -> factory.create(ctx, entity, pt))
				.skipVanillaRender(e -> renderNormally != null && !renderNormally.test(e))
				.apply();
		});
	}

	@FunctionalInterface
	public interface VisualFactory<T extends Entity> {
		AbstractEntityVisual<? super T> create(VisualizationContext ctx, T entity, float partialTick);
	}
}
