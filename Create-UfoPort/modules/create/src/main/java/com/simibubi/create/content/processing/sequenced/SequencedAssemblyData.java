package com.simibubi.create.content.processing.sequenced;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Typed DataComponent for tracking sequenced assembly progress on transitional items.
 * Replaces the old CompoundTag with nested "SequencedAssembly" sub-key.
 */
public record SequencedAssemblyData(ResourceLocation id, int step, float progress) {

	public static final Codec<SequencedAssemblyData> CODEC = RecordCodecBuilder.create(i -> i.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(SequencedAssemblyData::id),
		Codec.INT.fieldOf("Step").forGetter(SequencedAssemblyData::step),
		Codec.FLOAT.fieldOf("Progress").forGetter(SequencedAssemblyData::progress)
	).apply(i, SequencedAssemblyData::new));

	public static final StreamCodec<ByteBuf, SequencedAssemblyData> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SequencedAssemblyData::id,
		ByteBufCodecs.INT, SequencedAssemblyData::step,
		ByteBufCodecs.FLOAT, SequencedAssemblyData::progress,
		SequencedAssemblyData::new
	);
}
