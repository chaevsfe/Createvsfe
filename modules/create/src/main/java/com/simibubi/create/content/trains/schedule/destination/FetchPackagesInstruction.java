package com.simibubi.create.content.trains.schedule.destination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime.State;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.StringUtils;

public class FetchPackagesInstruction extends TextScheduleInstruction {

	@Override
	public Pair<ItemStack, Component> getSummary() {
		return Pair.of(getSecondLineIcon(), Lang.translateDirect("schedule.instruction.package_retrieval"));
	}

	@Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(Lang.translate("schedule.instruction.package_retrieval.summary")
			.style(ChatFormatting.GOLD)
			.component(),
			Lang.translateDirect("generic.in_quotes", Component.literal(getLabelText())),
			Lang.translateDirect("schedule.instruction.package_retrieval.summary_1")
				.withStyle(ChatFormatting.GRAY),
			Lang.translateDirect("schedule.instruction.package_retrieval.summary_2")
				.withStyle(ChatFormatting.GRAY));
	}

	@Override
	public ItemStack getSecondLineIcon() {
		return PackageStyles.getDefaultBox();
	}

	public String getFilter() {
		return getLabelText();
	}

	public String getFilterForRegex() {
		String filter = getFilter();
		if (filter.isBlank())
			return ".*";
		// Convert glob pattern (with * wildcards) to regex
		return "\\Q" + filter.replace("*", "\\E.*\\Q") + "\\E";
	}

	@Override
	public List<Component> getSecondLineTooltip(int slot) {
		return ImmutableList.of(Lang.translateDirect("schedule.instruction.address_filter_edit_box"),
			Lang.translateDirect("schedule.instruction.address_filter_edit_box_1")
				.withStyle(ChatFormatting.GRAY),
			Lang.translateDirect("schedule.instruction.address_filter_edit_box_2")
				.withStyle(ChatFormatting.DARK_GRAY),
			Lang.translateDirect("schedule.instruction.address_filter_edit_box_3")
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void modifyEditBox(EditBox box) {
		box.setFilter(s -> StringUtils.countMatches(s, '*') <= 3);
	}

	@Override
	public ResourceLocation getId() {
		return Create.asResource("package_retrieval");
	}

	@Override
	public boolean supportsConditions() {
		return true;
	}

	@Override
	public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
		MinecraftServer server = level.getServer();
		if (server == null)
			return null;

		String regex = getFilterForRegex();
		boolean anyMatch = false;
		ArrayList<GlobalStation> validStations = new ArrayList<>();
		Train train = runtime.train;

		if (!train.hasForwardConductor() && !train.hasBackwardConductor()) {
			train.status.missingConductor();
			runtime.startCooldown();
			return null;
		}

		for (GlobalStation globalStation : train.graph.getPoints(EdgePointType.STATION)) {
			ServerLevel dimLevel = server.getLevel(globalStation.blockEntityDimension);
			if (dimLevel == null)
				continue;

			for (Entry<BlockPos, GlobalPackagePort> entry : globalStation.connectedPorts.entrySet()) {
				GlobalPackagePort port = entry.getValue();
				BlockPos pos = entry.getKey();

				// Check live postbox inventory if loaded, otherwise use offline buffer
				int slotCount;
				java.util.function.IntFunction<ItemStack> slotGetter;

				if (dimLevel.isLoaded(pos) && dimLevel.getBlockEntity(pos) instanceof PostboxBlockEntity ppbe) {
					var inv = ppbe.inventory;
					slotCount = inv.getSlotCount();
					slotGetter = inv::getStackInSlot;
				} else {
					var buf = port.offlineBuffer;
					slotCount = buf.getSlotCount();
					slotGetter = buf::getStackInSlot;
				}

				for (int slot = 0; slot < slotCount; slot++) {
					ItemStack stack = slotGetter.apply(slot);
					if (!PackageItem.isPackage(stack))
						continue;
					if (PackageItem.matchAddress(stack, port.address))
						continue;
					try {
						if (!PackageItem.getAddress(stack).matches(regex))
							continue;
						anyMatch = true;
						if (!validStations.contains(globalStation))
							validStations.add(globalStation);
					} catch (PatternSyntaxException ignored) {
					}
				}
			}
		}

		if (validStations.isEmpty()) {
			runtime.startCooldown();
			runtime.state = State.PRE_TRANSIT;
			runtime.currentEntry++;
			return null;
		}

		DiscoveredPath best = train.navigation.findPathTo(validStations, Double.MAX_VALUE);
		if (best == null) {
			if (anyMatch)
				train.status.failedNavigation();
			runtime.startCooldown();
			return null;
		}

		return best;
	}
}
