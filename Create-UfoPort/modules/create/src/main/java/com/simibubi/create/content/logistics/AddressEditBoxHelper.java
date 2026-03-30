package com.simibubi.create.content.logistics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;
import com.simibubi.create.foundation.utility.IntAttached;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AddressEditBoxHelper {

	private static final Map<BlockPos, WeakReference<ClipboardBlockEntity>> NEARBY_CLIPBOARDS = new ConcurrentHashMap<>();

	public static void advertiseClipboard(ClipboardBlockEntity blockEntity) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		BlockPos blockPos = blockEntity.getBlockPos();
		if (player.distanceToSqr(Vec3.atCenterOf(blockPos)) > 32 * 32)
			return;
		NEARBY_CLIPBOARDS.put(blockPos, new WeakReference<>(blockEntity));
	}

	public static DestinationSuggestions createSuggestions(Screen screen, EditBox pInput, boolean anchorToBottom, String localAddress) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		List<IntAttached<String>> options = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();

		DestinationSuggestions destinationSuggestions = new DestinationSuggestions(mc, screen, pInput, mc.font, options,
			-72 + pInput.getY() + pInput.getHeight());

		if (player == null)
			return destinationSuggestions;

		if (localAddress != null) {
			options.add(IntAttached.with(-1, localAddress));
			alreadyAdded.add(localAddress);
		}

		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
			appendAddresses(options, alreadyAdded, player.getInventory().getItem(i));

		for (WeakReference<ClipboardBlockEntity> wr : NEARBY_CLIPBOARDS.values()) {
			ClipboardBlockEntity cbe = wr.get();
			if (cbe != null)
				appendAddressesFromBE(options, alreadyAdded, cbe);
		}

		return destinationSuggestions;
	}

	private static void appendAddresses(List<IntAttached<String>> options, Set<String> alreadyAdded, ItemStack item) {
		if (item == null || !AllBlocks.CLIPBOARD.isIn(item))
			return;

		// UfoPort's ClipboardEntry.readAll takes ItemStack
		List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(item);
		appendFromPages(options, alreadyAdded, pages);
	}

	private static void appendAddressesFromBE(List<IntAttached<String>> options, Set<String> alreadyAdded, ClipboardBlockEntity cbe) {
		// Save block entity data to a temporary stack and read from it
		ItemStack tempStack = AllBlocks.CLIPBOARD.asStack();
		if (cbe.getLevel() != null)
			cbe.saveToItem(tempStack, cbe.getLevel().registryAccess());
		List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(tempStack);
		appendFromPages(options, alreadyAdded, pages);
	}

	private static void appendFromPages(List<IntAttached<String>> options, Set<String> alreadyAdded, List<List<ClipboardEntry>> pages) {
		pages.forEach(page -> page.forEach(entry -> {
			String string = entry.text.getString();
			if (entry.checked)
				return;
			if (!string.startsWith("#") || string.length() == 1)
				return;
			String address = string.substring(1);
			if (address.isBlank())
				return;
			String trim = address.trim();
			if (!alreadyAdded.add(trim))
				return;
			options.add(IntAttached.withZero(trim));
		}));
	}

}
