package com.simibubi.create.content.logistics.filter;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Standalone enum for attribute filter whitelist/blacklist mode.
 * Matches NeoForge's AttributeFilterWhitelistMode.
 * Mirrors the existing inner enum {@link AttributeFilterMenu.WhitelistMode}.
 */
public enum AttributeFilterWhitelistMode {
	WHITELIST_DISJ,
	WHITELIST_CONJ,
	BLACKLIST;

	public static final Codec<AttributeFilterWhitelistMode> CODEC =
		Codec.STRING.xmap(AttributeFilterWhitelistMode::valueOf, Enum::name);
	public static final StreamCodec<ByteBuf, AttributeFilterWhitelistMode> STREAM_CODEC =
		CreateStreamCodecs.ofEnum(AttributeFilterWhitelistMode.class);
}
