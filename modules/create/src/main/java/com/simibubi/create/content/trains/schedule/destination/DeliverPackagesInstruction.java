package com.simibubi.create.content.trains.schedule.destination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime.State;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DeliverPackagesInstruction extends ScheduleInstruction {

	@Override
	public Pair<ItemStack, Component> getSummary() {
		return Pair.of(getSecondLineIcon(), Lang.translateDirect("schedule.instruction.package_delivery"));
	}

	@Override
	public ItemStack getSecondLineIcon() {
		return AllBlocks.PACKAGE_POSTBOX.asStack();
	}

	@Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(Lang.translate("schedule.instruction.package_delivery.summary")
			.style(ChatFormatting.GOLD)
			.component(),
			Lang.translateDirect("schedule.instruction.package_delivery.summary_1")
				.withStyle(ChatFormatting.GRAY),
			Lang.translateDirect("schedule.instruction.package_delivery.summary_2")
				.withStyle(ChatFormatting.GRAY));
	}

	@Override
	public ResourceLocation getId() {
		return Create.asResource("package_delivery");
	}

	@Override
	public boolean supportsConditions() {
		return true;
	}

	@Override
	@Nullable
	public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
		boolean anyMatch = false;
		String firstPackage = null;
		ArrayList<GlobalStation> validStations = new ArrayList<>();
		Train train = runtime.train;

		if (!train.hasForwardConductor() && !train.hasBackwardConductor()) {
			train.status.missingConductor();
			runtime.startCooldown();
			return null;
		}

		for (Carriage carriage : train.carriages) {
			if (carriage.storage == null)
				continue;
			var items = carriage.storage.getItems();

			try (Transaction t = TransferUtil.getTransaction()) {
				for (StorageView<ItemVariant> view : items.nonEmptyViews()) {
					ItemStack stack = view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
					if (!PackageItem.isPackage(stack))
						continue;
					if (firstPackage == null)
						firstPackage = PackageItem.getAddress(stack);
					for (GlobalStation globalStation : train.graph.getPoints(EdgePointType.STATION)) {
						for (Entry<BlockPos, GlobalPackagePort> port : globalStation.connectedPorts.entrySet()) {
							if (!PackageItem.matchAddress(stack, port.getValue().address))
								continue;
							anyMatch = true;
							if (!validStations.contains(globalStation))
								validStations.add(globalStation);
							break;
						}
					}
				}
			}
		}

		if (validStations.isEmpty()) {
			if (firstPackage != null) {
				train.status.failedNavigationNoTarget(firstPackage);
				runtime.startCooldown();
			} else {
				runtime.state = State.PRE_TRANSIT;
				runtime.currentEntry++;
			}
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
