package com.simibubi.create.impl.contraption.storage;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.world.level.block.Block;

public enum MountedItemStorageFallbackProvider implements SimpleRegistry.Provider<Block, MountedItemStorageType<?>> {
	INSTANCE;

	@Override
	@Nullable
	public MountedItemStorageType<?> get(Block block) {
		return AllTags.AllBlockTags.FALLBACK_MOUNTED_STORAGE_BLACKLIST.matches(block)
			? null
			: AllMountedStorageTypes.FALLBACK.get();
	}

	@Override
	public void onRegister(Runnable invalidate) {
		// On Fabric, invalidate the registry cache when tags are reloaded
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> invalidate.run());
	}
}
