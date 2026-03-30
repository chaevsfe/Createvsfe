package com.hlysine.create_connected;

import com.hlysine.create_connected.content.fluidvessel.FluidVesselMountedStorageType;
import com.hlysine.create_connected.content.itemsilo.ItemSiloMountedStorageType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CCMountedStorageTypes {

    public static final ItemSiloMountedStorageType SILO = registerItem("silo", ItemSiloMountedStorageType::new);
    public static final FluidVesselMountedStorageType FLUID_VESSEL = registerFluid("fluid_vessel", FluidVesselMountedStorageType::new);

    private static <T extends MountedItemStorageType<?>> T registerItem(String name, Supplier<T> supplier) {
        T instance = supplier.get();
        Registry.register(CreateBuiltInRegistries.MOUNTED_ITEM_STORAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(CreateConnected.MODID, name), instance);
        return instance;
    }

    private static <T extends MountedFluidStorageType<?>> T registerFluid(String name, Supplier<T> supplier) {
        T instance = supplier.get();
        Registry.register(CreateBuiltInRegistries.MOUNTED_FLUID_STORAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(CreateConnected.MODID, name), instance);
        return instance;
    }

    public static void register() {
    }
}
