package com.simibubi.create.foundation.utility;

import java.util.Comparator;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class IntAttached<V> extends Pair<Integer, V> {

	protected IntAttached(Integer first, V second) {
		super(first, second);
	}

	public static <V> IntAttached<V> with(int number, V value) {
		return new IntAttached<>(number, value);
	}

	public static <V> IntAttached<V> withZero(V value) {
		return new IntAttached<>(0, value);
	}

	public boolean isZero() {
		return first.intValue() == 0;
	}

	public boolean exceeds(int value) {
		return first.intValue() > value;
	}

	public boolean isOrBelowZero() {
		return first.intValue() <= 0;
	}

	public void increment() {
		first++;
	}

	public void decrement() {
		first--;
	}

	public V getValue() {
		return getSecond();
	}

	public CompoundTag serializeNBT(Function<V, CompoundTag> serializer) {
		CompoundTag nbt = new CompoundTag();
		nbt.put("Item", serializer.apply(getValue()));
		nbt.putInt("Location", getFirst());
		return nbt;
	}

	public static Comparator<? super IntAttached<?>> comparator() {
		return (i1, i2) -> Integer.compare(i2.getFirst(), i1.getFirst());
	}

	public static <T> IntAttached<T> read(CompoundTag nbt, Function<CompoundTag, T> deserializer) {
		return IntAttached.with(nbt.getInt("Location"), deserializer.apply(nbt.getCompound("Item")));
	}

	public static <T> Codec<IntAttached<T>> codec(Codec<T> valueCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("count").forGetter(IntAttached::getFirst),
			valueCodec.fieldOf("value").forGetter(IntAttached::getValue)
		).apply(instance, IntAttached::with));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, IntAttached<T>> streamCodec(StreamCodec<B, T> valueCodec) {
		return StreamCodec.composite(
			ByteBufCodecs.INT, IntAttached::getFirst,
			valueCodec, IntAttached::getValue,
			IntAttached::with
		);
	}

}
