package com.simibubi.create.content.logistics.filter;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.filter.attribute.ItemAttributeType;
import com.simibubi.create.foundation.utility.Lang;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemAttribute {
	Codec<ItemAttribute> CODEC = CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE.byNameCodec()
		.dispatch(ItemAttribute::getType, ItemAttributeType::codec);
	StreamCodec<RegistryFriendlyByteBuf, ItemAttribute> STREAM_CODEC = ByteBufCodecs.registry(CreateRegistries.ITEM_ATTRIBUTE_TYPE)
		.dispatch(ItemAttribute::getType, ItemAttributeType::streamCodec);

	static CompoundTag saveStatic(ItemAttribute attribute, HolderLookup.Provider registries) {
		CompoundTag nbt = new CompoundTag();
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
		CODEC.encodeStart(ops, attribute)
			.resultOrPartial(err -> {})
			.ifPresent(tag -> nbt.put("attribute", tag));
		return nbt;
	}

	@Nullable
	static ItemAttribute loadStatic(CompoundTag nbt, HolderLookup.Provider registries) {
		if (!nbt.contains("attribute"))
			return null;
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
		return CODEC.parse(ops, nbt.get("attribute"))
			.resultOrPartial(err -> {})
			.orElse(null);
	}

	static List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
		List<ItemAttribute> attributes = new ArrayList<>();
		for (ItemAttributeType type : CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE) {
			attributes.addAll(type.getAllAttributes(stack, level));
		}
		return attributes;
	}

	boolean appliesTo(ItemStack stack, Level world);

	ItemAttributeType getType();

	@Environment(value = EnvType.CLIENT)
	default MutableComponent format(boolean inverted) {
		return Lang.translateDirect("item_attributes." + getTranslationKey() + (inverted ? ".inverted" : ""),
			getTranslationParameters());
	}

	String getTranslationKey();

	default Object[] getTranslationParameters() {
		return new String[0];
	}

	record ItemAttributeEntry(ItemAttribute attribute, boolean inverted) {
		public static final Codec<ItemAttributeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
			ItemAttribute.CODEC.fieldOf("attribute").forGetter(ItemAttributeEntry::attribute),
			Codec.BOOL.fieldOf("inverted").forGetter(ItemAttributeEntry::inverted)
		).apply(i, ItemAttributeEntry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeEntry> STREAM_CODEC = StreamCodec.composite(
			ItemAttribute.STREAM_CODEC, ItemAttributeEntry::attribute,
			ByteBufCodecs.BOOL, ItemAttributeEntry::inverted,
			ItemAttributeEntry::new
		);
	}
}
