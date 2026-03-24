package com.simibubi.create.foundation.codec;

import java.util.Vector;
import java.util.function.BiFunction;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

/**
 * Utility stream codecs for types that lack built-in STREAM_CODEC fields.
 * Fabric equivalent of CatnipStreamCodecs from the NeoForge Catnip library.
 */
public interface CreateStreamCodecs {

	StreamCodec<ByteBuf, Rotation> ROTATION = StreamCodec.of(
		(buf, rot) -> buf.writeInt(rot.ordinal()),
		buf -> Rotation.values()[buf.readInt()]
	);

	StreamCodec<ByteBuf, Mirror> MIRROR = StreamCodec.of(
		(buf, mirror) -> buf.writeInt(mirror.ordinal()),
		buf -> Mirror.values()[buf.readInt()]
	);

	StreamCodec<ByteBuf, Vec3i> VEC3I = StreamCodec.of(
		(buf, v) -> { buf.writeInt(v.getX()); buf.writeInt(v.getY()); buf.writeInt(v.getZ()); },
		buf -> new Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
	);

	StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.of(
		(buf, v) -> { buf.writeDouble(v.x); buf.writeDouble(v.y); buf.writeDouble(v.z); },
		buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
	);

	/**
	 * Create a StreamCodec for an enum type using ordinal encoding.
	 * Fabric equivalent of CatnipStreamCodecBuilders.ofEnum().
	 */
	static <E extends Enum<E>> StreamCodec<ByteBuf, E> ofEnum(Class<E> enumClass) {
		E[] values = enumClass.getEnumConstants();
		return StreamCodec.of(
			(buf, val) -> buf.writeInt(val.ordinal()),
			buf -> values[buf.readInt()]
		);
	}

	/**
	 * @deprecated Vector should be replaced with list
	 */
	@Deprecated(forRemoval = true)
	static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Vector<V>> vector() {
		return codec -> ByteBufCodecs.collection(Vector::new, codec);
	}

	/**
	 * @deprecated All uses should be converted to proper codecs
	 */
	@Deprecated(forRemoval = true)
	static <C> StreamCodec<RegistryFriendlyByteBuf, C> ofLegacyNbtWithRegistries(
			BiFunction<C, HolderLookup.Provider, CompoundTag> writer,
			BiFunction<HolderLookup.Provider, CompoundTag, C> reader
	) {
		return new StreamCodec<>() {
			@Override
			public C decode(RegistryFriendlyByteBuf buffer) {
				return reader.apply(buffer.registryAccess(), buffer.readNbt());
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, C value) {
				buffer.writeNbt(writer.apply(value, buffer.registryAccess()));
			}
		};
	}

	/**
	 * Creates a StreamCodec that handles nullable values by writing a boolean presence flag.
	 * Replacement for CatnipStreamCodecBuilders.nullable().
	 */
	public static <B extends net.minecraft.network.FriendlyByteBuf, V> StreamCodec<B, @org.jetbrains.annotations.Nullable V> nullable(StreamCodec<B, V> codec) {
		return new StreamCodec<>() {
			@Override
			public @org.jetbrains.annotations.Nullable V decode(B buf) {
				return buf.readBoolean() ? codec.decode(buf) : null;
			}

			@Override
			public void encode(B buf, @org.jetbrains.annotations.Nullable V value) {
				buf.writeBoolean(value != null);
				if (value != null) {
					codec.encode(buf, value);
				}
			}
		};
	}
}
