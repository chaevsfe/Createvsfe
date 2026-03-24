package com.simibubi.create.content.logistics.packagePort;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PackagePortItem extends BlockItem {

	public PackagePortItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack stack,
		BlockState state) {
		// TODO: send PackagePortPlacementPacket when target selection system is ported
		return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
	}
}
