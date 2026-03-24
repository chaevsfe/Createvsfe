package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneRequesterScreen extends AbstractSimiContainerScreen<RedstoneRequesterMenu> {

	private AllGuiTextures background;

	public RedstoneRequesterScreen(RedstoneRequesterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		background = AllGuiTextures.ATTRIBUTE_FILTER; // TODO: proper texture
	}

	@Override
	protected void init() {
		setWindowSize(background.width, background.height + 98);
		super.init();
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int x = leftPos;
		int y = topPos;
		background.render(graphics, x, y);
		renderPlayerInventory(graphics, x + 12, y + background.height + 8);
	}
}
