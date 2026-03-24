package com.simibubi.create.api.registry;

import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.impl.registry.SimpleRegistryImpl;
import com.simibubi.create.impl.registry.TagProviderImpl;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.Fluid;

/**
 * A simple registry mapping between objects with identity semantics.
 * Provides simple registration functionality, as well as lazy providers.
 * This class is thread-safe, and may be safely used during parallel mod init.
 */
@ApiStatus.NonExtendable
public interface SimpleRegistry<K, V> {
	void register(K object, V value);

	void registerProvider(Provider<K, V> provider);

	void invalidate();

	@Nullable
	V get(K object);

	@Nullable
	V get(StateHolder<K, ?> state);

	static <K, V> SimpleRegistry<K, V> create() {
		return SimpleRegistryImpl.single();
	}

	@FunctionalInterface
	interface Provider<K, V> {
		@Nullable
		V get(K object);

		default void onRegister(Runnable invalidate) {
		}

		static <K, V> Provider<K, V> forTag(TagKey<K> tag, Function<K, Holder<K>> holderGetter, V value) {
			return new TagProviderImpl<>(tag, holderGetter, value);
		}

		@SuppressWarnings("deprecation")
		static <V> Provider<Block, V> forBlockTag(TagKey<Block> tag, V value) {
			return new TagProviderImpl<>(tag, Block::builtInRegistryHolder, value);
		}

		static <V> Provider<BlockEntityType<?>, V> forBlockEntityTag(TagKey<BlockEntityType<?>> tag, V value) {
			return new TagProviderImpl<>(tag, TagProviderImpl::getBeHolder, value);
		}

		@SuppressWarnings("deprecation")
		static <V> Provider<Item, V> forItemTag(TagKey<Item> tag, V value) {
			return new TagProviderImpl<>(tag, Item::builtInRegistryHolder, value);
		}

		@SuppressWarnings("deprecation")
		static <V> Provider<EntityType<?>, V> forEntityTag(TagKey<EntityType<?>> tag, V value) {
			return new TagProviderImpl<>(tag, EntityType::builtInRegistryHolder, value);
		}

		@SuppressWarnings("deprecation")
		static <V> Provider<Fluid, V> forFluidTag(TagKey<Fluid> tag, V value) {
			return new TagProviderImpl<>(tag, Fluid::builtInRegistryHolder, value);
		}
	}

	interface Multi<K, V> extends SimpleRegistry<K, List<V>> {
		void add(K object, V value);

		void addProvider(Provider<K, V> provider);

		@Override
		@NotNull
		List<V> get(K object);

		@Override
		@NotNull
		List<V> get(StateHolder<K, ?> state);

		static <K, V> Multi<K, V> create() {
			return SimpleRegistryImpl.multi();
		}
	}
}
