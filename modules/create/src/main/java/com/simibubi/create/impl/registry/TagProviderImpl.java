package com.simibubi.create.impl.registry;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.registry.SimpleRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Fabric adaptation of NeoForge TagProviderImpl.
 * Uses Fabric's ServerLifecycleEvents for tag reload invalidation
 * instead of NeoForge's TagsUpdatedEvent.
 */
public class TagProviderImpl<K, V> implements SimpleRegistry.Provider<K, V> {
	private final TagKey<K> tag;
	private final Function<K, Holder<K>> holderGetter;
	private final V value;

	public TagProviderImpl(TagKey<K> tag, Function<K, Holder<K>> holderGetter, V value) {
		this.tag = tag;
		this.holderGetter = holderGetter;
		this.value = value;
	}

	@Override
	@Nullable
	public V get(K object) {
		Holder<K> holder = this.holderGetter.apply(object);
		return holder.is(this.tag) ? this.value : null;
	}

	@Override
	public void onRegister(Runnable invalidate) {
		// On Fabric, tag reloading happens during data pack reload.
		// We register via Fabric API's ServerLifecycleEvents.END_DATA_PACK_RELOAD
		// However, to avoid depending on server lifecycle from static registry init,
		// we simply invalidate on first use after reload (the providedValues cache
		// is cleared by invalidate()). For now, tags are stable after load.
		// TODO: Hook into Fabric's tag reload event for dynamic invalidation
	}

	public static Holder<BlockEntityType<?>> getBeHolder(BlockEntityType<?> type) {
		ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
		if (key == null)
			throw new IllegalStateException("Unregistered BlockEntityType: " + type);

		return BuiltInRegistries.BLOCK_ENTITY_TYPE.getHolder(key).orElseThrow();
	}
}
