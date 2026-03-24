package com.simibubi.create.content.logistics.filter.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.filter.ItemAttribute;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import io.netty.buffer.ByteBuf;

public record FluidContentsAttribute(@Nullable Fluid fluid) implements ItemAttribute {
	public static final MapCodec<FluidContentsAttribute> CODEC = BuiltInRegistries.FLUID.byNameCodec()
		.xmap(FluidContentsAttribute::new, FluidContentsAttribute::fluid)
		.fieldOf("value");

	public static final StreamCodec<ByteBuf, FluidContentsAttribute> STREAM_CODEC = StreamCodec.of(
		(buf, attr) -> ResourceLocation.STREAM_CODEC.encode(buf, BuiltInRegistries.FLUID.getKey(attr.fluid())),
		buf -> new FluidContentsAttribute(BuiltInRegistries.FLUID.get(ResourceLocation.STREAM_CODEC.decode(buf)))
	);

	private static List<Fluid> extractFluids(ItemStack stack) {
		List<Fluid> fluids = new ArrayList<>();
		Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
		if (storage != null) {
			for (StorageView<FluidVariant> view : storage) {
				if (!view.isResourceBlank() && view.getAmount() > 0) {
					fluids.add(view.getResource().getFluid());
				}
			}
		}
		return fluids;
	}

	@Override
	public boolean appliesTo(ItemStack itemStack, Level level) {
		return extractFluids(itemStack).contains(fluid);
	}

	@Override
	public String getTranslationKey() {
		return "has_fluid";
	}

	@Override
	public Object[] getTranslationParameters() {
		String parameter = "";
		if (fluid != null)
			parameter = FluidVariantAttributes.getName(FluidVariant.of(fluid)).getString();
		return new Object[]{parameter};
	}

	@Override
	public ItemAttributeType getType() {
		return AllItemAttributeTypes.HAS_FLUID;
	}

	public static class Type implements ItemAttributeType {
		@Override
		public @NotNull ItemAttribute createAttribute() {
			return new FluidContentsAttribute(null);
		}

		@Override
		public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
			List<ItemAttribute> list = new ArrayList<>();
			for (Fluid fluid : extractFluids(stack)) {
				list.add(new FluidContentsAttribute(fluid));
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
