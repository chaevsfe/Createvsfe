package com.simibubi.create.content.logistics.packagePort;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class PackagePortTarget {
	public static final Codec<PackagePortTarget> CODEC = CreateBuiltInRegistries.PACKAGE_PORT_TARGET_TYPE.byNameCodec()
		.dispatch(PackagePortTarget::getType, PackagePortTargetType::codec);
	public static final StreamCodec<RegistryFriendlyByteBuf, PackagePortTarget> STREAM_CODEC = ByteBufCodecs.registry(CreateRegistries.PACKAGE_PORT_TARGET_TYPE)
		.<PackagePortTarget>dispatch(PackagePortTarget::getType, PackagePortTargetType::streamCodec);

	public BlockPos relativePos;

	public PackagePortTarget(BlockPos relativePos) {
		this.relativePos = relativePos;
	}

	public abstract boolean export(LevelAccessor level, BlockPos portPos, ItemStack box, boolean simulate);

	public void setup(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {}

	public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {}

	public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {}

	public abstract Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos);

	public abstract ItemStack getIcon();

	public abstract boolean canSupport(BlockEntity be);

	public boolean depositImmediately() {
		return false;
	}

	protected abstract PackagePortTargetType getType();

	public BlockEntity be(LevelAccessor level, BlockPos portPos) {
		if (level instanceof Level l && !l.isLoaded(portPos.offset(relativePos)))
			return null;
		return level.getBlockEntity(portPos.offset(relativePos));
	}

	// ChainConveyorFrogportTarget is stubbed until Chain Conveyor is ported (Phase 4)
	public static class ChainConveyorFrogportTarget extends PackagePortTarget {
		public static final MapCodec<ChainConveyorFrogportTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("relative_pos").forGetter(i -> i.relativePos),
			Codec.FLOAT.fieldOf("chain_pos").forGetter(i -> i.chainPos),
			BlockPos.CODEC.optionalFieldOf("connection").forGetter(i -> Optional.ofNullable(i.connection)),
			Codec.BOOL.fieldOf("flipped").forGetter(i -> i.flipped)
		).apply(instance, ChainConveyorFrogportTarget::new));

		public static final StreamCodec<ByteBuf, ChainConveyorFrogportTarget> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, i -> i.relativePos,
			ByteBufCodecs.FLOAT, i -> i.chainPos,
			BlockPos.STREAM_CODEC.map(Optional::of, o -> o.orElse(BlockPos.ZERO)), i -> Optional.ofNullable(i.connection),
			ByteBufCodecs.BOOL, i -> i.flipped,
			ChainConveyorFrogportTarget::new
		);

		public float chainPos;
		@Nullable
		public BlockPos connection;
		public boolean flipped;

		public ChainConveyorFrogportTarget(BlockPos relativePos, float chainPos, Optional<BlockPos> connection, boolean flipped) {
			this(relativePos, chainPos, connection.orElse(null), flipped);
		}

		public ChainConveyorFrogportTarget(BlockPos relativePos, float chainPos, @Nullable BlockPos connection, boolean flipped) {
			super(relativePos);
			this.chainPos = chainPos;
			this.connection = connection;
			this.flipped = flipped;
		}

		@Override
		public ItemStack getIcon() {
			// Chain Conveyor not yet ported — return empty for now
			return ItemStack.EMPTY;
		}

		@Override
		public boolean export(LevelAccessor level, BlockPos portPos, ItemStack box, boolean simulate) {
			// Chain Conveyor not yet ported
			return false;
		}

		@Override
		public Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			return Vec3.atCenterOf(portPos.offset(relativePos));
		}

		@Override
		public boolean canSupport(BlockEntity be) {
			return AllBlockEntityTypes.PACKAGE_FROGPORT.is(be);
		}

		@Override
		protected PackagePortTargetType getType() {
			return AllPackagePortTargetTypes.CHAIN_CONVEYOR;
		}

		public static class Type implements PackagePortTargetType {
			@Override
			public MapCodec<ChainConveyorFrogportTarget> codec() {
				return CODEC;
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, ChainConveyorFrogportTarget> streamCodec() {
				return STREAM_CODEC;
			}
		}
	}

	public static class TrainStationFrogportTarget extends PackagePortTarget {
		public static MapCodec<TrainStationFrogportTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockPos.CODEC.fieldOf("relative_pos").forGetter(i -> i.relativePos)
		).apply(instance, TrainStationFrogportTarget::new));

		public static final StreamCodec<ByteBuf, TrainStationFrogportTarget> STREAM_CODEC = BlockPos.STREAM_CODEC
			.map(TrainStationFrogportTarget::new, i -> i.relativePos);

		public TrainStationFrogportTarget(BlockPos relativePos) {
			super(relativePos);
		}

		@Override
		public ItemStack getIcon() {
			return AllBlocks.TRACK_STATION.asStack();
		}

		@Override
		public boolean export(LevelAccessor level, BlockPos portPos, ItemStack box, boolean simulate) {
			return false;
		}

		@Override
		public Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			return Vec3.atCenterOf(portPos.offset(relativePos));
		}

		@Override
		public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			if (be(level, portPos) instanceof StationBlockEntity sbe)
				sbe.attachPackagePort(ppbe);
		}

		@Override
		public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
			if (be(level, portPos) instanceof StationBlockEntity sbe)
				sbe.removePackagePort(ppbe);
		}

		@Override
		public boolean depositImmediately() {
			return true;
		}

		@Override
		public boolean canSupport(BlockEntity be) {
			return AllBlockEntityTypes.PACKAGE_POSTBOX.is(be);
		}

		@Override
		protected PackagePortTargetType getType() {
			return AllPackagePortTargetTypes.TRAIN_STATION;
		}

		public static class Type implements PackagePortTargetType {
			@Override
			public MapCodec<TrainStationFrogportTarget> codec() {
				return CODEC;
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, TrainStationFrogportTarget> streamCodec() {
				return STREAM_CODEC;
			}
		}
	}
}
