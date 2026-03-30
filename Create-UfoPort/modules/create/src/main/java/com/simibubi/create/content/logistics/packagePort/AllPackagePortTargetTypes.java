package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget.ChainConveyorFrogportTarget;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget.TrainStationFrogportTarget;

import net.minecraft.core.Registry;

public class AllPackagePortTargetTypes {
	public static final PackagePortTargetType CHAIN_CONVEYOR = register("chain_conveyor", new ChainConveyorFrogportTarget.Type());
	public static final PackagePortTargetType TRAIN_STATION = register("train_station", new TrainStationFrogportTarget.Type());

	private static PackagePortTargetType register(String id, PackagePortTargetType type) {
		return Registry.register(CreateBuiltInRegistries.PACKAGE_PORT_TARGET_TYPE, Create.asResource(id), type);
	}

	public static void init() {
	}
}
