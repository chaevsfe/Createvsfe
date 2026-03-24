package com.simibubi.create.content.logistics.stockTicker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Stock Keeper Category screen — UI for configuring stock ticker
 * category filters (which item groups to display to customers).
 * Stub implementation — full GUI will be ported when the Stock Keeper
 * system's rendering and widget components are available.
 */
public class StockKeeperCategoryScreen extends AbstractContainerScreen<StockKeeperCategoryMenu> {

	public StockKeeperCategoryScreen(StockKeeperCategoryMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		// Stub — will render category configuration UI
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
}
