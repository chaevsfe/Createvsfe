package com.simibubi.create.api.contraption.storage.item.simple;

import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.foundation.codec.CreateCodecs;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Widely-applicable mounted storage implementation.
 * Gets an item storage from the mounted block (via Fabric Transfer API), copies it to an ItemStackHandler,
 * and then copies the inventory back to the target when unmounting.
 * <br>
 * To use this implementation, either register {@link AllMountedStorageTypes#SIMPLE} to your block
 * manually, or add your block to the {@link AllTags.AllBlockTags#SIMPLE_MOUNTED_STORAGE} tag.
 */
public class SimpleMountedStorage extends WrapperMountedItemStorage<ItemStackHandler> {
	public static final MapCodec<SimpleMountedStorage> CODEC = codec(SimpleMountedStorage::new);

	public SimpleMountedStorage(MountedItemStorageType<?> type, ItemStackHandler handler) {
		super(type, handler);
	}

	public SimpleMountedStorage(ItemStackHandler handler) {
		this(AllMountedStorageTypes.SIMPLE.get(), handler);
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (be == null)
			return;

		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, state, be, Direction.UP);
		if (storage == null) {
			// try without direction
			storage = ItemStorage.SIDED.find(level, pos, state, be, null);
		}
		if (storage != null) {
			final Storage<ItemVariant> finalStorage = storage;
			validate(finalStorage).ifPresent(handler -> {
				for (int i = 0; i < handler.getSlotCount(); i++) {
					handler.setStackInSlot(i, this.getStackInSlot(i));
				}
			});
		}
	}

	/**
	 * Make sure the targeted storage is valid for copying items back into.
	 */
	protected Optional<ItemStackHandler> validate(Storage<ItemVariant> storage) {
		if (storage instanceof ItemStackHandler handler && handler.getSlotCount() == this.getSlotCount()) {
			return Optional.of(handler);
		}
		return Optional.empty();
	}

	public static <T extends SimpleMountedStorage> MapCodec<T> codec(Function<ItemStackHandler, T> factory) {
		return CreateCodecs.ITEM_STACK_HANDLER.xmap(factory, storage -> storage.wrapped).fieldOf("value");
	}
}
