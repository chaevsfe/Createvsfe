package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Handles player interaction with seated Stock Keeper NPCs.
 * On Fabric, this is registered as an entity interact callback.
 *
 * Simplified from NeoForge version — shopping list fulfillment deferred
 * until Table Cloth/ShoppingListItem system is ported.
 */
public class StockTickerInteractionHandler {

	public static void register() {
		net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register(
			(player, world, hand, entity, hitResult) -> {
				if (player == null || entity == null)
					return InteractionResult.PASS;
				if (player.isSpectator())
					return InteractionResult.PASS;

				BlockPos targetPos = getStockTickerPosition(entity);
				if (targetPos == null)
					return InteractionResult.PASS;

				if (interactWithLogisticsManagerAt(player, world, targetPos))
					return InteractionResult.SUCCESS;
				return InteractionResult.PASS;
			});
	}

	public static boolean interactWithLogisticsManagerAt(Player player, Level level, BlockPos targetPos) {
		if (level.isClientSide())
			return true;
		if (!(level.getBlockEntity(targetPos) instanceof StockTickerBlockEntity stbe))
			return false;

		if (!stbe.behaviour.mayInteract(player)) {
			Lang.translate("stock_keeper.locked")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			return true;
		}

		if (player instanceof ServerPlayer sp) {
			// TODO: Full stock keeper menu opening with lock options
			// For now, send stock summary to player
			stbe.getRecentSummary()
				.divideAndSendTo(sp, targetPos);
		}

		return true;
	}

	public static BlockPos getStockTickerPosition(Entity entity) {
		Entity rootVehicle = entity.getRootVehicle();
		if (!(rootVehicle instanceof SeatEntity))
			return null;
		if (!(entity instanceof LivingEntity))
			return null;
		if (AllEntityTypes.PACKAGE.is(entity))
			return null;

		BlockPos pos = entity.blockPosition();
		int stations = 0;
		BlockPos targetPos = null;

		for (Direction d : Iterate.horizontalDirections) {
			for (int y : Iterate.zeroAndOne) {
				BlockPos workstationPos = pos.relative(d)
					.above(y);
				if (!(entity.level()
					.getBlockState(workstationPos)
					.getBlock() instanceof StockTickerBlock))
					continue;
				targetPos = workstationPos;
				stations++;
			}
		}

		if (stations != 1)
			return null;
		return targetPos;
	}

}
