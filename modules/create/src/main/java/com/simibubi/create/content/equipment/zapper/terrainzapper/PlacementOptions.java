package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum PlacementOptions {

	Merged(AllIcons.I_CENTERED),
	Attached(AllIcons.I_ATTACHED),
	Inserted(AllIcons.I_INSERTED);

	public static final Codec<PlacementOptions> CODEC = Codec.STRING.xmap(PlacementOptions::valueOf, Enum::name);
	public static final StreamCodec<ByteBuf, PlacementOptions> STREAM_CODEC = CreateStreamCodecs.ofEnum(PlacementOptions.class);

	public String translationKey;
	public AllIcons icon;

	private PlacementOptions(AllIcons icon) {
		this.translationKey = Lang.asId(name());
		this.icon = icon;
	}

}
