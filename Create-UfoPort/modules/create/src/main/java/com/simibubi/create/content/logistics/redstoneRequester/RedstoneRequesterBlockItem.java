package com.simibubi.create.content.logistics.redstoneRequester;

import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class RedstoneRequesterBlockItem extends LogisticallyLinkedBlockItem {

	public RedstoneRequesterBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		if (!isTuned(stack))
			return;

		if (!stack.has(AllDataComponents.AUTO_REQUEST_DATA)) {
			super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
			return;
		}

		tooltipComponents.add(Lang.translateDirect("logistically_linked.tooltip")
			.withStyle(ChatFormatting.GOLD));
		RedstoneRequesterBlock.appendRequesterTooltip(stack, tooltipComponents);
	}
}
