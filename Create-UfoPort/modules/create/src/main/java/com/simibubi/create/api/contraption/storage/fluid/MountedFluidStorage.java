package com.simibubi.create.api.contraption.storage.fluid;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Abstract mounted fluid storage for contraptions.
 * On Fabric, fluid storage is provided via Storage<FluidVariant> from the Fabric Transfer API.
 */
public abstract class MountedFluidStorage {
	public static final Codec<MountedFluidStorage> CODEC = MountedFluidStorageType.CODEC.dispatch(
		storage -> storage.type, type -> type.codec
	);

	@SuppressWarnings("deprecation")
	public static final StreamCodec<RegistryFriendlyByteBuf, MountedFluidStorage> STREAM_CODEC = StreamCodec.of(
		(b, t) -> b.writeWithCodec(RegistryOps.create(NbtOps.INSTANCE, b.registryAccess()), CODEC, t),
		b -> b.readWithCodecTrusted(RegistryOps.create(NbtOps.INSTANCE, b.registryAccess()), CODEC)
	);

	public final MountedFluidStorageType<? extends MountedFluidStorage> type;

	protected MountedFluidStorage(MountedFluidStorageType<?> type) {
		this.type = Objects.requireNonNull(type);
	}

	/**
	 * Get the fluid storage for this mounted fluid storage.
	 * This is the Fabric Transfer API storage used for fluid interactions.
	 */
	public abstract Storage<FluidVariant> getStorage();

	/**
	 * Un-mount this storage back into the world. The expected storage type of the target
	 * block has already been checked to make sure it matches this storage's type.
	 */
	public abstract void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be);
}
