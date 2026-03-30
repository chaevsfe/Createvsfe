package com.mrh0.createaddition.energy.fabric;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric BlockApiLookup for energy storage, replacing EnergyLookup.ENERGY.
 */
public class EnergyLookup {
    public static final BlockApiLookup<IEnergyStorage, Direction> ENERGY =
            BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath("createaddition", "energy"), IEnergyStorage.class, Direction.class);
}
