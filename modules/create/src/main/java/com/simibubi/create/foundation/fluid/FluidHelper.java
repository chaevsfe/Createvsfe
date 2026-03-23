package com.simibubi.create.foundation.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.Pair;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FluidHelper {

	public static enum FluidExchange {
		ITEM_TO_TANK, TANK_TO_ITEM;
	}

	public static boolean isWater(Fluid fluid) {
		return convertToStill(fluid) == Fluids.WATER;
	}

	public static boolean isLava(Fluid fluid) {
		return convertToStill(fluid) == Fluids.LAVA;
	}

	@SuppressWarnings("deprecation")
	public static boolean isTag(Fluid fluid, TagKey<Fluid> tag) {
		return fluid.is(tag);
	}

	public static boolean isTag(FluidState fluid, TagKey<Fluid> tag) {
		return fluid.is(tag);
	}

	public static boolean isTag(FluidStack fluid, TagKey<Fluid> tag) {
		return isTag(fluid.getFluid(), tag);
	}

	public static SoundEvent getFillSound(FluidStack fluid) {
		return FluidVariantAttributes.getFillSound(fluid.getType());
	}

	public static SoundEvent getEmptySound(FluidStack fluid) {
		return FluidVariantAttributes.getEmptySound(fluid.getType());
	}

	public static boolean hasBlockState(Fluid fluid) {
		return !fluid.defaultFluidState().createLegacyBlock().isAir();
	}

	public static FluidStack copyStackWithAmount(FluidStack fs, long amount) {
		if (amount <= 0)
			return FluidStack.EMPTY;
		if (fs.isEmpty())
			return FluidStack.EMPTY;
		FluidStack copy = fs.copy();
		copy.setAmount(amount);
		return copy;
	}

	public static Fluid convertToFlowing(Fluid fluid) {
		if (fluid == Fluids.WATER)
			return Fluids.FLOWING_WATER;
		if (fluid == Fluids.LAVA)
			return Fluids.FLOWING_LAVA;
		if (fluid instanceof FlowingFluid)
			return ((FlowingFluid) fluid).getFlowing();
		return fluid;
	}

	public static Fluid convertToStill(Fluid fluid) {
		if (fluid == Fluids.FLOWING_WATER)
			return Fluids.WATER;
		if (fluid == Fluids.FLOWING_LAVA)
			return Fluids.LAVA;
		if (fluid instanceof FlowingFluid)
			return ((FlowingFluid) fluid).getSource();
		return fluid;
	}

	public static JsonElement serializeFluidStack(FluidStack stack) {
		return FluidStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).getOrThrow();
	}

	public static FluidStack deserializeFluidStack(JsonObject json) {
		return FluidStack.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
	}

	public static boolean tryEmptyItemIntoBE(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
		SmartBlockEntity be, Direction side) {
		if (!GenericItemEmptying.canItemBeEmptied(worldIn, heldItem))
			return false;

		Pair<FluidStack, ItemStack> emptyingResult = GenericItemEmptying.emptyItem(worldIn, heldItem, true);

		Storage<FluidVariant> tank = FluidStorage.SIDED.find(worldIn, be.getBlockPos(), null, be, side);
		FluidStack fluidStack = emptyingResult.getFirst();

		if (tank == null)
			return false;
		if (worldIn.isClientSide)
			return true;

		try (Transaction t = TransferUtil.getTransaction()) {
			long inserted = tank.insert(fluidStack.getType(), fluidStack.getAmount(), t);
			if (inserted != fluidStack.getAmount())
				return false;

			ItemStack copyOfHeld = heldItem.copy();
			emptyingResult = GenericItemEmptying.emptyItem(worldIn, copyOfHeld, false);
			t.commit();

			if (!player.isCreative() && !(be instanceof CreativeFluidTankBlockEntity)) {
				if (copyOfHeld.isEmpty())
					player.setItemInHand(handIn, emptyingResult.getSecond());
				else {
					player.setItemInHand(handIn, copyOfHeld);
					player.getInventory().placeItemBackInInventory(emptyingResult.getSecond());
				}
			}
			return true;
		}
	}

	public static boolean tryFillItemFromBE(Level world, Player player, InteractionHand handIn, ItemStack heldItem,
		SmartBlockEntity be, Direction side) {
		if (!GenericItemFilling.canItemBeFilled(world, heldItem))
			return false;

		Storage<FluidVariant> tank = FluidStorage.SIDED.find(world, be.getBlockPos(), null, be, side);

		if (tank == null)
			return false;

		try (Transaction t = TransferUtil.getTransaction()) {
			for (FluidStack fluid : TransferUtil.getAllFluids(tank)) {
				if (fluid.isEmpty())
					continue;
				long requiredAmountForItem = GenericItemFilling.getRequiredAmountForItem(world, heldItem, fluid.copy());
				if (requiredAmountForItem == -1)
					continue;
				if (requiredAmountForItem > fluid.getAmount())
					continue;

				if (world.isClientSide)
					return true;

				if (player.isCreative() || be instanceof CreativeFluidTankBlockEntity)
					heldItem = heldItem.copy();
				ItemStack out = GenericItemFilling.fillItem(world, requiredAmountForItem, heldItem, fluid.copy());

				FluidStack copy = fluid.copy();
				copy.setAmount(requiredAmountForItem);
				tank.extract(copy.getType(), copy.getAmount(), t);
				t.commit();

				if (!player.isCreative())
					player.getInventory().placeItemBackInInventory(out);
				be.notifyUpdate();
				return true;
			}
		}

		return false;
	}

}
