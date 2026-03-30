package com.simibubi.create.api.equipment.goggles;

import java.util.List;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.infrastructure.config.AllConfigs;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib_ufo.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib_ufo.util.FluidUnit;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Implement this interface on the {@link BlockEntity} that wants to add info to the goggle overlay
 */
public non-sealed interface IHaveGoggleInformation extends IHaveCustomOverlayIcon {
	/**
	 * This method will be called when looking at a {@link BlockEntity} that implements this interface
	 *
	 * @return {@code true} if the tooltip creation was successful and should be
	 * displayed, or {@code false} if the overlay should not be displayed
	 */
	default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

	default boolean containedFluidTooltip(List<Component> tooltip, boolean isPlayerSneaking,
		Storage<FluidVariant> handler) {
		if (handler == null)
			return false;

		FluidUnit unit = AllConfigs.client().fluidUnitType.get();
		LangBuilder mb = Lang.translate("generic.unit.millibuckets");
		Lang.translate("gui.goggles.fluid_container")
			.forGoggles(tooltip);

		boolean isEmpty = true;
		for (StorageView<FluidVariant> view : handler) {
			if (view.isResourceBlank())
				continue;

			FluidStack fluidStack = new FluidStack(view.getResource(), view.getAmount());
			if (fluidStack.isEmpty())
				continue;

			Lang.fluidName(fluidStack)
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip, 1);

			Lang.builder()
				.add(Lang.builder().text(FluidTextUtil.getUnicodeMillibuckets(fluidStack.getAmount(), unit, false))
					.add(mb)
					.style(ChatFormatting.GOLD))
				.text(ChatFormatting.GRAY, " / ")
				.add(Lang.builder().text(FluidTextUtil.getUnicodeMillibuckets(view.getCapacity(), unit, false))
					.add(mb)
					.style(ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);

			isEmpty = false;
		}

		if (!isEmpty)
			return true;

		return false;
	}
}
