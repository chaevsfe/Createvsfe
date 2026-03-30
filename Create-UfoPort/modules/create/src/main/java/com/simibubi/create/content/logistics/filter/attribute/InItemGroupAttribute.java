package com.simibubi.create.content.logistics.filter.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.ItemAttribute;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InItemGroupAttribute implements ItemAttribute {
	public static final MapCodec<InItemGroupAttribute> CODEC = BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec()
		.xmap(InItemGroupAttribute::new, i -> i.group)
		.fieldOf("value");

	public static final StreamCodec<ByteBuf, InItemGroupAttribute> STREAM_CODEC = StreamCodec.of(
		(buf, attr) -> {
			ResourceLocation key = attr.group == null ? null : BuiltInRegistries.CREATIVE_MODE_TAB.getKey(attr.group);
			buf.writeBoolean(key != null);
			if (key != null) ResourceLocation.STREAM_CODEC.encode(buf, key);
		},
		buf -> {
			if (!buf.readBoolean()) return new InItemGroupAttribute(null);
			return new InItemGroupAttribute(BuiltInRegistries.CREATIVE_MODE_TAB.get(ResourceLocation.STREAM_CODEC.decode(buf)));
		}
	);

	@Nullable
	private CreativeModeTab group;

	public InItemGroupAttribute(@Nullable CreativeModeTab group) {
		this.group = group;
	}

	private static boolean tabContainsItem(CreativeModeTab tab, ItemStack stack) {
		return tab.contains(stack) || tab.contains(new ItemStack(stack.getItem()));
	}

	@Override
	public boolean appliesTo(ItemStack stack, Level world) {
		if (group == null)
			return false;

		if (group.getDisplayItems().isEmpty()
			&& group.getSearchTabDisplayItems().isEmpty()) {
			try {
				group.buildContents(new CreativeModeTab.ItemDisplayParameters(world.enabledFeatures(), false,
					world.registryAccess()));
			} catch (RuntimeException | LinkageError e) {
				Create.LOGGER.error("Attribute Filter: Item Group {} crashed while building contents.",
					group.getDisplayName().getString(), e);
				group = null;
				return false;
			}
		}

		return tabContainsItem(group, stack);
	}

	@Override
	public String getTranslationKey() {
		return "in_item_group";
	}

	@Override
	public Object[] getTranslationParameters() {
		return new Object[]{group == null ? "<none>" : group.getDisplayName().getString()};
	}

	@Override
	public ItemAttributeType getType() {
		return AllItemAttributeTypes.IN_ITEM_GROUP;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InItemGroupAttribute that)) return false;
		return Objects.equals(group, that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(group);
	}

	public static class Type implements ItemAttributeType {
		@Override
		public @NotNull ItemAttribute createAttribute() {
			return new InItemGroupAttribute(null);
		}

		@Override
		public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
			List<ItemAttribute> list = new ArrayList<>();
			for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
				if (tab.getType() == CreativeModeTab.Type.CATEGORY && tabContainsItem(tab, stack)) {
					list.add(new InItemGroupAttribute(tab));
				}
			}
			return list;
		}

		@Override
		public MapCodec<? extends ItemAttribute> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
