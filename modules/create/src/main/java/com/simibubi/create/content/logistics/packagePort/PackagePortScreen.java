package com.simibubi.create.content.logistics.packagePort;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PackagePortScreen extends AbstractSimiContainerScreen<PackagePortMenu> {

	private AllGuiTextures background;

	private EditBox addressBox;
	private IconButton confirmButton;

	private List<Rect2i> extraAreas = Collections.emptyList();

	public PackagePortScreen(PackagePortMenu container, Inventory inv, Component title) {
		super(container, inv, title);
		background = AllGuiTextures.FROGPORT_BG;
	}

	@Override
	protected void init() {
		setWindowSize(background.width, background.height + 98);
		super.init();
		clearWidgets();

		int x = leftPos;
		int y = topPos;

		addressBox = new EditBox(font, x + 23, y + 5, background.width - 46, 10, Component.empty());
		addressBox.setBordered(false);
		addressBox.setMaxLength(25);
		addressBox.setTextColor(0xF3EBDE);
		addressBox.setValue(menu.contentHolder.addressFilter);
		addRenderableWidget(addressBox);

		confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> minecraft.player.closeContainer());
		addRenderableWidget(confirmButton);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
		int x = leftPos;
		int y = topPos;
		background.render(graphics, x, y);

		int invX = leftPos + 30;
		int invY = topPos + 8 + imageHeight - 98;
		renderPlayerInventory(graphics, invX, invY);
	}

	@Override
	public void removed() {
		AllPackets.getChannel().sendToServer(
			new PackagePortConfigurationPacket(menu.contentHolder.getBlockPos(), addressBox.getValue(), true));
		super.removed();
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}
}
