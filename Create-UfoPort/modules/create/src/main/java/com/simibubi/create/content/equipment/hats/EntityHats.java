package com.simibubi.create.content.equipment.hats;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityHats {

	@Nullable
	public static PartialModel getHatFor(LivingEntity entity) {
		if (entity == null)
			return null;
		ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (!headItem.isEmpty())
			return null;

		if (shouldRenderTrainHat(entity))
			return AllPartialModels.TRAIN_HAT;

		return getLogisticsHatFor(entity);
	}

	@Nullable
	public static PartialModel getLogisticsHatFor(LivingEntity entity) {
		if (!entity.isPassenger())
			return null;
		if (!(entity.getVehicle() instanceof SeatEntity))
			return null;

		int stations = 0;
		Level level = entity.level();
		BlockPos pos = entity.blockPosition();
		PartialModel hat = null;

		for (Direction d : Iterate.horizontalDirections) {
			for (int y : Iterate.zeroAndOne) {
				BlockPos checkPos = pos.relative(d).above(y);
				if (!(level.getBlockState(checkPos).getBlock() instanceof StockTickerBlock ticker))
					continue;
				PartialModel hatOfStation = ticker.getHat(level, checkPos, entity);
				if (hatOfStation == null)
					continue;
				hat = hatOfStation;
				stations++;
			}
		}

		if (stations == 1)
			return hat;

		return null;
	}

	public static boolean shouldRenderTrainHat(LivingEntity entity) {
		// Fabric: getCustomData() replaces NeoForge's getPersistentData()
		if (entity.getCustomData().contains("TrainHat"))
			return true;
		if (!entity.isPassenger())
			return false;
		if (!(entity.getVehicle() instanceof CarriageContraptionEntity cce))
			return false;
		if (!cce.hasSchedule() && !(entity instanceof Player))
			return false;
		Contraption contraption = cce.getContraption();
		if (!(contraption instanceof CarriageContraption cc))
			return false;
		BlockPos seatOf = cc.getSeatOf(entity.getUUID());
		if (seatOf == null)
			return false;
		Couple<Boolean> validSides = cc.conductorSeats.get(seatOf);
		return validSides != null;
	}

}
