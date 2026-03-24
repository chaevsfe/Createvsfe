package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Factory Panel configuration screen.
 * TODO: Full GUI implementation requires AddressEditBox, AllGuiTextures factory panel entries,
 *       and PackageStyles. This is a functional stub that sends configuration packets.
 */
public class FactoryPanelScreen extends AbstractSimiScreen {

	private IconButton confirmButton;
	private FactoryPanelBehaviour behaviour;
	private boolean restocker;
	private boolean sendReset;
	private boolean sendRedstoneReset;

	private BigItemStack outputConfig;
	private List<BigItemStack> inputConfig;
	private List<FactoryPanelConnection> connections;

	private CraftingRecipe availableCraftingRecipe;
	private boolean craftingActive;
	private List<BigItemStack> craftingIngredients;

	private String addressValue = "";
	private int promiseExpirationState = -1;

	public FactoryPanelScreen(FactoryPanelBehaviour behaviour) {
		this.behaviour = behaviour;
		minecraft = Minecraft.getInstance();
		restocker = behaviour.panelBE().restocker;
		availableCraftingRecipe = null;
		craftingActive = !behaviour.activeCraftingArrangement.isEmpty();
		addressValue = behaviour.recipeAddress;
		promiseExpirationState = behaviour.promiseClearingInterval;
		updateConfigs();
	}

	private void updateConfigs() {
		connections = new ArrayList<>(behaviour.targetedBy.values());
		outputConfig = new BigItemStack(behaviour.getFilter(), behaviour.recipeOutput);
		inputConfig = connections.stream()
			.map(c -> {
				FactoryPanelBehaviour b = FactoryPanelBehaviour.at(minecraft.level, c.from);
				return b == null ? new BigItemStack(ItemStack.EMPTY, 0) : new BigItemStack(b.getFilter(), c.amount);
			})
			.toList();

		searchForCraftingRecipe();

		if (availableCraftingRecipe == null) {
			craftingActive = false;
			return;
		}

		craftingIngredients = convertRecipeToPackageOrderContext(availableCraftingRecipe, inputConfig, false);
	}

	public static List<BigItemStack> convertRecipeToPackageOrderContext(CraftingRecipe availableCraftingRecipe, List<BigItemStack> inputs, boolean respectAmounts) {
		List<BigItemStack> craftingIngredients = new ArrayList<>();
		BigItemStack emptyIngredient = new BigItemStack(ItemStack.EMPTY, 1);
		NonNullList<Ingredient> ingredients = availableCraftingRecipe.getIngredients();
		List<BigItemStack> mutableInputs = BigItemStack.duplicateWrappers(inputs);

		int width = Math.min(3, ingredients.size());
		int height = Math.min(3, ingredients.size() / 3 + 1);

		if (availableCraftingRecipe instanceof ShapedRecipe shaped) {
			width = shaped.getWidth();
			height = shaped.getHeight();
		}

		if (height == 1)
			for (int i = 0; i < 3; i++)
				craftingIngredients.add(emptyIngredient);
		if (width == 1)
			craftingIngredients.add(emptyIngredient);

		for (int i = 0; i < ingredients.size(); i++) {
			Ingredient ingredient = ingredients.get(i);
			BigItemStack craftingIngredient = emptyIngredient;

			if (!ingredient.isEmpty())
				for (BigItemStack bigItemStack : mutableInputs)
					if (bigItemStack.count > 0 && ingredient.test(bigItemStack.stack)) {
						craftingIngredient = new BigItemStack(bigItemStack.stack, 1);
						if (respectAmounts)
							bigItemStack.count -= 1;
						break;
					}

			craftingIngredients.add(craftingIngredient);

			if (width < 3 && (i + 1) % width == 0)
				for (int j = 0; j < 3 - width; j++)
					if (craftingIngredients.size() < 9)
						craftingIngredients.add(emptyIngredient);
		}

		while (craftingIngredients.size() < 9)
			craftingIngredients.add(emptyIngredient);

		return craftingIngredients;
	}

	@Override
	protected void init() {
		// TODO: Use proper AllGuiTextures dimensions when available
		int sizeX = 256;
		int sizeY = 200;

		setWindowSize(sizeX, sizeY);
		super.init();
		clearWidgets();

		int x = guiLeft;
		int y = guiTop;

		confirmButton = new IconButton(x + sizeX - 33, y + sizeY - 25, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> minecraft.setScreen(null));
		confirmButton.setToolTip(Lang.translate("gui.factory_panel.save_and_close")
			.component());
		addRenderableWidget(confirmButton);
	}

	@Override
	public void tick() {
		super.tick();
		if (inputConfig.size() != behaviour.targetedBy.size()) {
			updateConfigs();
			init();
		}
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		// TODO: Full rendering requires AllGuiTextures factory panel entries
		// Render a basic title
		Component title = Lang
			.translate(restocker ? "gui.factory_panel.title_as_restocker" : "gui.factory_panel.title_as_recipe")
			.component();
		graphics.drawString(font, title, x + 97 - font.width(title) / 2, y + 4, 0x3D3C48, false);

		// Render input items
		int slot = 0;
		if (craftingActive && craftingIngredients != null) {
			for (BigItemStack itemStack : craftingIngredients)
				renderInputItem(graphics, slot++, itemStack);
		} else {
			for (BigItemStack itemStack : inputConfig)
				renderInputItem(graphics, slot++, itemStack);
		}

		// Render output
		if (!restocker && !outputConfig.stack.isEmpty()) {
			int outputX = x + 160;
			int outputY = y + 48;
			graphics.renderItem(outputConfig.stack, outputX, outputY);
			graphics.renderItemDecorations(font, behaviour.getFilter(), outputX, outputY, outputConfig.count + "");
		}
	}

	private void renderInputItem(GuiGraphics graphics, int slot, BigItemStack itemStack) {
		int inputX = guiLeft + (restocker ? 88 : 68 + (slot % 3 * 20));
		int inputY = guiTop + (restocker ? 12 : 28) + (slot / 3 * 20);
		graphics.renderItem(itemStack.stack, inputX, inputY);
		if (!craftingActive && !restocker && !itemStack.stack.isEmpty())
			graphics.renderItemDecorations(font, itemStack.stack, inputX, inputY, itemStack.count + "");
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		int x = guiLeft;
		int y = guiTop;

		if (craftingActive)
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

		for (int i = 0; i < inputConfig.size(); i++) {
			int inputX = x + 68 + (i % 3 * 20);
			int inputY = y + 26 + (i / 3 * 20);
			if (mouseX >= inputX && mouseX < inputX + 16 && mouseY >= inputY && mouseY < inputY + 16) {
				BigItemStack itemStack = inputConfig.get(i);
				if (itemStack.stack.isEmpty())
					return true;
				itemStack.count =
					Mth.clamp((int) (itemStack.count + Math.signum(scrollY) * (hasShiftDown() ? 10 : 1)), 1, 64);
				return true;
			}
		}

		if (!restocker) {
			int outputX = x + 160;
			int outputY = y + 48;
			if (mouseX >= outputX && mouseX < outputX + 16 && mouseY >= outputY && mouseY < outputY + 16) {
				BigItemStack itemStack = outputConfig;
				itemStack.count =
					Mth.clamp((int) (itemStack.count + Math.signum(scrollY) * (hasShiftDown() ? 10 : 1)), 1, 64);
				return true;
			}
		}

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public void removed() {
		sendIt(null, false);
		super.removed();
	}

	private void sendIt(@Nullable FactoryPanelPosition toRemove, boolean clearPromises) {
		Map<FactoryPanelPosition, Integer> inputs = new HashMap<>();

		if (inputConfig.size() == connections.size())
			for (int i = 0; i < inputConfig.size(); i++) {
				BigItemStack stackInConfig = inputConfig.get(i);
				inputs.put(connections.get(i).from, craftingActive && craftingIngredients != null ? (int) craftingIngredients.stream()
					.filter(
						b -> !b.stack.isEmpty() && ItemStack.isSameItemSameComponents(b.stack, stackInConfig.stack))
					.count() : stackInConfig.count);
			}

		List<ItemStack> craftingArrangement = craftingActive && craftingIngredients != null ? craftingIngredients.stream()
			.map(b -> b.stack)
			.toList() : List.of();

		FactoryPanelPosition pos = behaviour.getPanelPosition();

		FactoryPanelConfigurationPacket packet = new FactoryPanelConfigurationPacket(pos, addressValue, inputs,
			craftingArrangement, outputConfig.count, promiseExpirationState, toRemove, clearPromises, sendReset, sendRedstoneReset);
		AllPackets.getChannel().sendToServer(packet);
	}

	private void searchForCraftingRecipe() {
		ItemStack output = outputConfig.stack;
		if (output.isEmpty())
			return;
		if (behaviour.targetedBy.isEmpty())
			return;

		Set<Item> itemsToUse = inputConfig.stream()
			.map(b -> b.stack)
			.filter(i -> !i.isEmpty())
			.map(i -> i.getItem())
			.collect(Collectors.toSet());

		ClientLevel level = Minecraft.getInstance().level;

		availableCraftingRecipe = level.getRecipeManager()
			.getAllRecipesFor(RecipeType.CRAFTING)
			.parallelStream()
			.filter(r -> output.getItem() == r.value().getResultItem(level.registryAccess())
				.getItem())
			.filter(r -> {
				if (AllRecipeTypes.shouldIgnoreInAutomation(r.value()))
					return false;

				Set<Item> itemsUsed = new HashSet<>();
				for (Ingredient ingredient : r.value().getIngredients()) {
					if (ingredient.isEmpty())
						continue;
					boolean available = false;
					for (BigItemStack bis : inputConfig) {
						if (!bis.stack.isEmpty() && ingredient.test(bis.stack)) {
							available = true;
							itemsUsed.add(bis.stack.getItem());
							break;
						}
					}
					if (!available)
						return false;
				}

				if (itemsUsed.size() < itemsToUse.size())
					return false;

				return true;
			})
			.findAny()
			.map(RecipeHolder::value)
			.orElse(null);
	}

}
