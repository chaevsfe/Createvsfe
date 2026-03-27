package com.simibubi.create.compat.computercraft;

import java.util.function.Function;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ComputerCraftProxy {

	public static void register() {
		fallbackFactory = FallbackComputerBehaviour::new;
		Mods.COMPUTERCRAFT.executeIfInstalled(() -> ComputerCraftProxy::registerWithDependency);
	}

	private static void registerWithDependency() {
		computerFactory = ComputerBehaviour::new;
		ComputerBehaviour.registerItemDetailProviders();
		registerPeripheralProviders();
	}

	/**
	 * Register IPeripheral providers for all Create block entities that have CC compat.
	 * On Fabric, CC:Tweaked uses BlockApiLookup (via PeripheralLookup) instead of NeoForge capabilities.
	 */
	private static void registerPeripheralProviders() {
		var lookup = PeripheralLookup.get();

		// Kinetics
		registerSmartBE(lookup, AllBlockEntityTypes.MOTOR.get());
		registerSmartBE(lookup, AllBlockEntityTypes.ROTATION_SPEED_CONTROLLER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.SPEEDOMETER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.STRESSOMETER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.SEQUENCED_GEARSHIFT.get());

		// Redstone
		registerSmartBE(lookup, AllBlockEntityTypes.DISPLAY_LINK.get());
		registerSmartBE(lookup, AllBlockEntityTypes.NIXIE_TUBE.get());

		// Contraptions
		registerSmartBE(lookup, AllBlockEntityTypes.STICKER.get());

		// Trains
		registerSmartBE(lookup, AllBlockEntityTypes.TRACK_STATION.get());
		registerSmartBE(lookup, AllBlockEntityTypes.TRACK_SIGNAL.get());
		registerSmartBE(lookup, AllBlockEntityTypes.TRACK_OBSERVER.get());

		// High Logistics
		registerSmartBE(lookup, AllBlockEntityTypes.STOCK_TICKER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.PACKAGER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.REPACKAGER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.REDSTONE_REQUESTER.get());
		registerSmartBE(lookup, AllBlockEntityTypes.PACKAGE_POSTBOX.get());
		registerSmartBE(lookup, AllBlockEntityTypes.PACKAGE_FROGPORT.get());
		registerSmartBE(lookup, AllBlockEntityTypes.TABLE_CLOTH.get());
	}

	@SuppressWarnings("unchecked")
	private static <T extends SmartBlockEntity> void registerSmartBE(
			net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup<IPeripheral, Direction> lookup,
			BlockEntityType<?> type) {
		lookup.registerForBlockEntity((be, direction) -> {
			if (be instanceof SmartBlockEntity sbe) {
				AbstractComputerBehaviour behaviour = sbe.getBehaviour(AbstractComputerBehaviour.TYPE);
				if (behaviour != null)
					return behaviour.getPeripheralCapability();
			}
			return null;
		}, type);
	}

	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

	public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
		if (computerFactory == null)
			return fallbackFactory.apply(sbe);
		return computerFactory.apply(sbe);
	}

}
