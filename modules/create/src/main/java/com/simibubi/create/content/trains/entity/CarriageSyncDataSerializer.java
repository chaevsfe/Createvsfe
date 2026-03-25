package com.simibubi.create.content.trains.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class CarriageSyncDataSerializer implements EntityDataSerializer<CarriageSyncData> {

	private static final StreamCodec<ByteBuf, CarriageSyncData> STREAM_CODEC = StreamCodec.of(
			(buf, data) -> data.write(new FriendlyByteBuf(buf)),
			buf -> {
				CarriageSyncData data = new CarriageSyncData();
				data.read(new FriendlyByteBuf(buf));
				return data;
			}
	);

	@Override
	public StreamCodec<? super ByteBuf, CarriageSyncData> codec() {
		return STREAM_CODEC;
	}

	@Override
	public CarriageSyncData copy(CarriageSyncData data) {
		return data.copy();
	}

}
