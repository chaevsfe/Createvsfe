package io.github.fabricators_of_create.porting_lib_ufo.util;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib_ufo.mixin.accessors.common.accessor.MinecraftServerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

public final class MinecraftServerUtil {
	public static LevelStorageSource.LevelStorageAccess getAnvilConverterForAnvilFile(MinecraftServer minecraftServer) {
		return get(minecraftServer).port_lib$getStorageSource();
	}

	private static MinecraftServerAccessor get(MinecraftServer minecraftServer) {
		return MixinHelper.cast(minecraftServer);
	}

	private MinecraftServerUtil() {}
}
