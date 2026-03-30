package com.simibubi.create.impl.unpacking;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Unpacking handler for Mechanical Crafters.
 * Fabric adaptation: delegates to default handler since ConnectedInput.getInventories()
 * and crafter internals are not yet exposed. Full crafter-aware insertion will be added
 * when the Packager system's crafter integration is complete.
 */
public enum CrafterUnpackingHandler implements UnpackingHandler {
	INSTANCE;

	@Override
	public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
		// For now, delegate to default insertion handler.
		// Full crafter-aware placement (using crafting context to position items in specific
		// crafter slots) requires exposing ConnectedInput.getInventories() and
		// MechanicalCrafterBlockEntity.checkCompletedRecipe() — deferred.
		return DEFAULT.unpack(level, pos, state, side, items, null, simulate);
	}
}
