package com.simibubi.create.api.contraption.storage.item.simple;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimpleMountedStorageType<T extends SimpleMountedStorage> extends MountedItemStorageType<SimpleMountedStorage> {
	protected SimpleMountedStorageType(MapCodec<T> codec) {
		super(codec);
	}

	@Override
	@Nullable
	public SimpleMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		return Optional.ofNullable(be)
			.map(b -> getHandler(level, b))
			.map(this::createStorage)
			.orElse(null);
	}

	protected ItemStackHandler getHandler(Level level, BlockEntity be) {
		// Try Fabric Transfer API item storage
		for (Direction dir : new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, null}) {
			Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, be.getBlockPos(), be.getBlockState(), be, dir);
			if (storage instanceof ItemStackHandler handler) {
				return handler;
			}
		}
		return null;
	}

	protected SimpleMountedStorage createStorage(ItemStackHandler handler) {
		return new SimpleMountedStorage(this, handler);
	}

	public static final class Impl extends SimpleMountedStorageType<SimpleMountedStorage> {
		public Impl() {
			super(SimpleMountedStorage.CODEC);
		}
	}
}
