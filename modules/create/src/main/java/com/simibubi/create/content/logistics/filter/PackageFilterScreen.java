package com.simibubi.create.content.logistics.filter;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket.Option;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PackageFilterScreen extends AbstractFilterScreen<PackageFilterMenu> {

	private EditBox addressBox;
	private boolean deferFocus;

	public PackageFilterScreen(PackageFilterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, AllGuiTextures.FILTER);
		// TODO: Use PACKAGE_FILTER texture when available
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (deferFocus) {
			deferFocus = false;
			setFocused(addressBox);
		}
		// EditBox doesn't need tick() in MC 1.21.1
	}

	@Override
	protected void init() {
		super.init();

		int x = leftPos;
		int y = topPos;

		addressBox = new EditBox(this.font, x + 44, y + 28, 129, 9, Component.empty());
		addressBox.setTextColor(0xffffff);
		addressBox.setBordered(false);
		addressBox.setValue(menu.address != null ? menu.address : "");
		addressBox.setResponder(this::onAddressEdited);
		addRenderableWidget(addressBox);

		setFocused(addressBox);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);

		PoseStack ms = graphics.pose();
		ms.pushPose();
		ms.translate(leftPos + 16, topPos + 23, 0);
		GuiGameElement.of(PackageStyles.getDefaultBox())
			.render(graphics);
		ms.popPose();
	}

	public void onAddressEdited(String s) {
		menu.address = s;
		CompoundTag tag = new CompoundTag();
		tag.putString("Address", s);
		AllPackets.getChannel().sendToServer(new FilterScreenPacket(Option.UPDATE_ADDRESS, tag));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == GLFW.GLFW_KEY_ENTER)
			setFocused(null);
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

	@Override
	protected boolean isButtonEnabled(IconButton button) {
		return false;
	}

	@Override
	protected boolean isIndicatorOn(Indicator indicator) {
		return false;
	}

	protected int getTitleColor() {
		return 0x3D3C48;
	}
}
