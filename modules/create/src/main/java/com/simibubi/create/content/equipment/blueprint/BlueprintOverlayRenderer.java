package com.simibubi.create.content.equipment.blueprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity.BlueprintCraftingInventory;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity.BlueprintSection;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.content.logistics.filter.attribute.InTagAttribute;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.BlueprintOverlayShopContext;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem.ShoppingList;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Pair;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class BlueprintOverlayRenderer {

	static boolean active;
	static boolean empty;
	static boolean noOutput;
	static boolean lastSneakState;
	static BlueprintSection lastTargetedSection;

	static Map<ItemStack, ItemStack[]> cachedRenderedFilters = new IdentityHashMap<>();
	static List<Pair<ItemStack, Boolean>> ingredients = new ArrayList<>();
	static List<ItemStack> results = new ArrayList<>();
	static ItemStack result = ItemStack.EMPTY;
	static boolean resultCraftable = false;
	static BlueprintOverlayShopContext shopContext;

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();

		BlueprintSection last = lastTargetedSection;
		lastTargetedSection = null;
		active = false;
		noOutput = false;
		shopContext = null;

		if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
			return;

		HitResult mouseOver = mc.hitResult;
		if (mouseOver == null)
			return;
		if (mouseOver.getType() != Type.ENTITY)
			return;

		EntityHitResult entityRay = (EntityHitResult) mouseOver;
		if (!(entityRay.getEntity() instanceof BlueprintEntity))
			return;

		BlueprintEntity blueprintEntity = (BlueprintEntity) entityRay.getEntity();
		BlueprintSection sectionAt = blueprintEntity.getSectionAt(entityRay.getLocation()
			.subtract(blueprintEntity.position()));

		lastTargetedSection = last;
		active = true;

		boolean sneak = mc.player.isShiftKeyDown();
		if (sectionAt != lastTargetedSection || AnimationTickHolder.getTicks() % 10 == 0 || lastSneakState != sneak)
			rebuild(sectionAt, sneak);

		lastTargetedSection = sectionAt;
		lastSneakState = sneak;
	}

	public static void displayTrackRequirements(PlacementInfo info, ItemStack pavementItem) {
		if (active)
			return;

		active = true;
		empty = false;
		noOutput = true;
		ingredients.clear();

		int tracks = info.requiredTracks;
		while (tracks > 0) {
			ingredients.add(Pair.of(new ItemStack(info.trackMaterial.getBlock(), Math.min(64, tracks)), info.hasRequiredTracks));
			tracks -= 64;
		}

		int pavement = info.requiredPavement;
		while (pavement > 0) {
			ingredients.add(Pair.of(ItemHandlerHelper.copyStackWithSize(pavementItem, Math.min(64, pavement)),
				info.hasRequiredPavement));
			pavement -= 64;
		}
	}

	public static void displayChainRequirements(net.minecraft.world.item.Item chainItem, int count, boolean fulfilled) {
		if (active)
			return;

		active = true;
		empty = false;
		noOutput = true;
		ingredients.clear();

		int chains = count;
		while (chains > 0) {
			ingredients.add(Pair.of(new ItemStack(chainItem, Math.min(64, chains)), fulfilled));
			chains -= 64;
		}
	}

	public static void displayClothShop(TableClothBlockEntity dce, int alreadyPurchased, ShoppingList list) {
		if (active)
			return;
		prepareCustomOverlay();
		noOutput = false;

		shopContext = new BlueprintOverlayShopContext(false, dce.getStockLevelForTrade(list), alreadyPurchased);

		ingredients.add(Pair.of(dce.getPaymentItem().copyWithCount(dce.getPaymentAmount()),
			!dce.getPaymentItem().isEmpty() && shopContext.stockLevel() > shopContext.purchases()));
		for (BigItemStack entry : dce.requestData.encodedRequest().stacks())
			results.add(entry.stack.copyWithCount(entry.count));
	}

	public static void displayShoppingList(Couple<InventorySummary> bakedList) {
		if (active || bakedList == null)
			return;
		Minecraft mc = Minecraft.getInstance();
		prepareCustomOverlay();
		noOutput = false;

		shopContext = new BlueprintOverlayShopContext(true, 1, 0);

		for (BigItemStack entry : bakedList.getSecond().getStacksByCount())
			ingredients.add(Pair.of(entry.stack.copyWithCount(entry.count), canAfford(mc.player, entry)));

		for (BigItemStack entry : bakedList.getFirst().getStacksByCount())
			results.add(entry.stack.copyWithCount(entry.count));
	}

	private static boolean canAfford(Player player, BigItemStack entry) {
		int itemsPresent = 0;
		for (int i = 0; i < player.getInventory().items.size(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item.isEmpty() || !ItemStack.isSameItemSameComponents(item, entry.stack))
				continue;
			itemsPresent += item.getCount();
		}
		return itemsPresent >= entry.count;
	}

	private static void prepareCustomOverlay() {
		active = true;
		empty = false;
		noOutput = true;
		ingredients.clear();
		results.clear();
		result = ItemStack.EMPTY;
		shopContext = null;
	}

	public static void rebuild(BlueprintSection sectionAt, boolean sneak) {
		cachedRenderedFilters.clear();
		ItemStackHandler items = sectionAt.getItems();
		boolean empty = true;
		for (int i = 0; i < 9; i++) {
			if (!items.getStackInSlot(i)
				.isEmpty()) {
				empty = false;
				break;
			}
		}

		BlueprintOverlayRenderer.empty = empty;
		BlueprintOverlayRenderer.result = ItemStack.EMPTY;
		BlueprintOverlayRenderer.results.clear();

		if (empty)
			return;

		boolean firstPass = true;
		boolean success = true;
		Minecraft mc = Minecraft.getInstance();
		PlayerInventoryStorage playerInv = PlayerInventoryStorage.of(mc.player);

		int amountCrafted = 0;
		Optional<CraftingRecipe> recipe = Optional.empty();
		Map<Integer, ItemStack> craftingGrid = new HashMap<>();
		ingredients.clear();
		ItemStackHandler missingItems = new ItemStackHandler(64);
		ItemStackHandler availableItems = new ItemStackHandler(64);
		List<ItemStack> newlyAdded = new ArrayList<>();
		List<ItemStack> newlyMissing = new ArrayList<>();
		boolean invalid = false;

		try (Transaction t = TransferUtil.getTransaction()) {
			do {
				craftingGrid.clear();
				newlyAdded.clear();
				newlyMissing.clear();

				Search:for (int i = 0; i < 9; i++) {
					FilterItemStack requestedItem = FilterItemStack.of(items.getStackInSlot(i));
					if (requestedItem.isEmpty()) {
						craftingGrid.put(i, ItemStack.EMPTY);
						continue;
					}

					ResourceAmount<ItemVariant> resource = StorageUtil.findExtractableContent(
							playerInv, v -> requestedItem.test(mc.level, v.toStack()), t);
					if (resource != null) {
						ItemStack currentItem = resource.resource().toStack(1);
						craftingGrid.put(i, currentItem);
						newlyAdded.add(currentItem);
						continue Search;
					}

					success = false;
					newlyMissing.add(requestedItem.item());
				}

				if (success) {
					CraftingContainer craftingInventory = new BlueprintCraftingInventory(craftingGrid);
					if (!recipe.isPresent()) {
						Optional<RecipeHolder<CraftingRecipe>> opt = mc.level.getRecipeManager()
								.getRecipeFor(RecipeType.CRAFTING, craftingInventory.asCraftInput(), mc.level);
						if(opt.isPresent()) {
							recipe = Optional.of(opt.get().value());
						}
						
					}
						
					ItemStack resultFromRecipe = recipe.filter(r -> r.matches(craftingInventory.asCraftInput(), mc.level))
							.map(r -> r.assemble(craftingInventory.asCraftInput(), mc.level.registryAccess()))
							.orElse(ItemStack.EMPTY);

					if (resultFromRecipe.isEmpty()) {
						if (!recipe.isPresent())
							invalid = true;
						success = false;
					} else if (resultFromRecipe.getCount() + amountCrafted > 64) {
						success = false;
					} else {
						amountCrafted += resultFromRecipe.getCount();
						if (result.isEmpty()) {
							result = resultFromRecipe.copy();
							results.add(result);
						} else {
							result.grow(resultFromRecipe.getCount());
						}
						resultCraftable = true;
						firstPass = false;
					}
				}

				if (success || firstPass) {
					newlyAdded.forEach(s -> availableItems.insert(ItemVariant.of(s), s.getCount(), t));
					newlyMissing.forEach(s -> missingItems.insert(ItemVariant.of(s), s.getCount(), t));
				}

				if (!success) {
					if (firstPass) {
						results.clear();
						result = invalid ? ItemStack.EMPTY : items.getStackInSlot(9);
						if (!result.isEmpty()) results.add(result);
						resultCraftable = false;
					}
					break;
				}

				if (!sneak)
					break;

			} while (success);
			t.commit();
		}

		for (int i = 0; i < 9; i++) {
			ItemStack available = availableItems.getStackInSlot(i);
			if (available.isEmpty())
				continue;
			ingredients.add(Pair.of(available, true));
		}
		for (int i = 0; i < 9; i++) {
			ItemStack missing = missingItems.getStackInSlot(i);
			if (missing.isEmpty())
				continue;
			ingredients.add(Pair.of(missing, false));
		}
	}

	public static void renderOverlay(GuiGraphics graphics, float partialTicks, Window window) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui)
			return;
		if (!active || empty)
			return;

		boolean invalidShop = shopContext != null && (ingredients.isEmpty() || ingredients.get(0)
			.getFirst().isEmpty() || shopContext.stockLevel() == 0);

		int w = 21 * ingredients.size();

		if (!noOutput) {
			w += 21 * Math.max(1, results.size());
			w += 30;
		}

		int x = (window.getGuiScaledWidth() - w) / 2;
		int y = (int) (window.getGuiScaledHeight() - 100);

		for (Pair<ItemStack, Boolean> pair : ingredients) {
			RenderSystem.enableBlend();
			(pair.getSecond() ? AllGuiTextures.HOTSLOT_ACTIVE : AllGuiTextures.HOTSLOT).render(graphics, x, y);
			ItemStack itemStack = pair.getFirst();
			String count = shopContext != null && !shopContext.checkout() || pair.getSecond() ? null
				: ChatFormatting.GOLD.toString() + itemStack.getCount();
			drawItemStack(graphics, mc, x, y, itemStack, count);
			x += 21;
		}

		if (noOutput)
			return;

		x += 5;
		RenderSystem.enableBlend();
		if (invalidShop)
			AllGuiTextures.HOTSLOT_ARROW_BAD.render(graphics, x, y + 4);
		else
			AllGuiTextures.HOTSLOT_ARROW.render(graphics, x, y + 4);
		x += 25;

		if (results.isEmpty()) {
			AllGuiTextures.HOTSLOT.render(graphics, x, y);
			GuiGameElement.of(Items.BARRIER)
				.at(x + 3, y + 3)
				.render(graphics);
		} else {
			for (ItemStack res : results) {
				AllGuiTextures slot = resultCraftable ? AllGuiTextures.HOTSLOT_SUPER_ACTIVE : AllGuiTextures.HOTSLOT;
				if (!invalidShop && shopContext != null && shopContext.stockLevel() > shopContext.purchases())
					slot = AllGuiTextures.HOTSLOT_ACTIVE;
				slot.render(graphics, resultCraftable ? x - 1 : x, resultCraftable ? y - 1 : y);
				drawItemStack(graphics, mc, x, y, res, null);
				x += 21;
			}
		}
		RenderSystem.disableBlend();
	}

	public static void drawItemStack(GuiGraphics graphics, Minecraft mc, int x, int y, ItemStack itemStack, String count) {
		if (itemStack.getItem() instanceof FilterItem) {
			int step = AnimationTickHolder.getTicks(mc.level) / 10;
			ItemStack[] itemsMatchingFilter = getItemsMatchingFilter(itemStack);
			if (itemsMatchingFilter.length > 0)
				itemStack = itemsMatchingFilter[step % itemsMatchingFilter.length];
		}

		GuiGameElement.of(itemStack)
			.at(x + 3, y + 3)
			.render(graphics);
		graphics.renderItemDecorations(mc.font, itemStack, x + 3, y + 3, count);
	}

	private static ItemStack[] getItemsMatchingFilter(ItemStack filter) {
		return cachedRenderedFilters.computeIfAbsent(filter, itemStack -> {
			CompoundTag tag = ItemHelper.getOrCreateComponent(itemStack, AllDataComponents.FILTER_DATA, new CompoundTag());

			if (AllItems.FILTER.isIn(itemStack) && !tag.getBoolean("Blacklist")) {
				ItemStackHandler filterItems = FilterItem.getFilterItems(itemStack);
				List<ItemStack> list = new ArrayList<>();
				for (int slot = 0; slot < filterItems.getSlotCount(); slot++) {
					ItemStack stackInSlot = filterItems.getStackInSlot(slot);
					if (!stackInSlot.isEmpty())
						list.add(stackInSlot);
				}
				return list.toArray(new ItemStack[list.size()]);
			}

			if (AllItems.ATTRIBUTE_FILTER.isIn(itemStack)) {
				AttributeFilterWhitelistMode whitelistMode = itemStack.getOrDefault(
					AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, AttributeFilterWhitelistMode.WHITELIST_DISJ);
				java.util.List<ItemAttribute.ItemAttributeEntry> entries = itemStack.getOrDefault(
					AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, java.util.List.of());
				if (whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ && entries.size() == 1) {
					ItemAttribute attr = entries.get(0).attribute();
					if (attr instanceof InTagAttribute inTag) {
						List<ItemStack> stacks = new ArrayList<>();
						for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(inTag.tag())) {
							stacks.add(new ItemStack(holder.value()));
						}
						return stacks.toArray(ItemStack[]::new);
					}
				}
			}

			return new ItemStack[0];
		});
	}

}
