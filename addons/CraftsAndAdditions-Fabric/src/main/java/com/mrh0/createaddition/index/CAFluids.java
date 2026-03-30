package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.resources.ResourceLocation;

public class CAFluids {

    public static FluidEntry<?> SEED_OIL;
    public static FluidEntry<?> BIOETHANOL;

    public static void register() {
        SEED_OIL = CreateAddition.REGISTRATE.fluid("seed_oil",
                        ResourceLocation.fromNamespaceAndPath("createaddition", "fluid/seed_oil_still"),
                        ResourceLocation.fromNamespaceAndPath("createaddition", "fluid/seed_oil_flow"))
                .register();

        BIOETHANOL = CreateAddition.REGISTRATE.fluid("bioethanol",
                        ResourceLocation.fromNamespaceAndPath("createaddition", "fluid/bioethanol_still"),
                        ResourceLocation.fromNamespaceAndPath("createaddition", "fluid/bioethanol_flow"))
                .register();
    }
}
