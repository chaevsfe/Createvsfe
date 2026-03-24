package com.simibubi.create.content.logistics.stockTicker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Stock Keeper Request screen — the main UI for browsing inventory
 * and ordering packages from the logistics network.
 * Stub implementation — full GUI will be ported when the Stock Keeper
 * system's rendering and widget components are available.
 */
public class StockKeeperRequestScreen extends AbstractContainerScreen<StockKeeperRequestMenu> {

	public StockKeeperRequestScreen(StockKeeperRequestMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		// Stub — will render stock browsing UI
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
}
