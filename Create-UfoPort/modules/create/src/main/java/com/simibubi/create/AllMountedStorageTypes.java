package com.simibubi.create;

import java.util.function.Supplier;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.chest.ChestMountedStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.behaviour.dispenser.storage.DispenserMountedStorageType;
import com.simibubi.create.content.equipment.toolbox.ToolboxMountedStorageType;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorageType;
import com.simibubi.create.content.fluids.tank.storage.creative.CreativeFluidTankMountedStorageType;
import com.simibubi.create.content.logistics.crate.CreativeCrateMountedStorageType;
import com.simibubi.create.content.logistics.depot.storage.DepotMountedStorageType;
import com.simibubi.create.content.logistics.vault.ItemVaultMountedStorageType;
import com.simibubi.create.impl.contraption.storage.FallbackMountedStorageType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class AllMountedStorageTypes {
	// Item storage types
	public static final RegistryHolder<MountedItemStorageType<?>, FallbackMountedStorageType> FALLBACK =
		registerItem("fallback", FallbackMountedStorageType::new);

	public static final RegistryHolder<MountedItemStorageType<?>, CreativeCrateMountedStorageType> CREATIVE_CRATE =
		registerItem("creative_crate", CreativeCrateMountedStorageType::new);

	public static final RegistryHolder<MountedItemStorageType<?>, ItemVaultMountedStorageType> VAULT =
		registerItem("vault", ItemVaultMountedStorageType::new);

	public static final RegistryHolder<MountedItemStorageType<?>, ToolboxMountedStorageType> TOOLBOX =
		registerItem("toolbox", ToolboxMountedStorageType::new);

	public static final RegistryHolder<MountedItemStorageType<?>, DepotMountedStorageType> DEPOT =
		registerItem("depot", DepotMountedStorageType::new);

	public static final RegistryHolder<MountedItemStorageType<?>, SimpleMountedStorageType.Impl> SIMPLE;
	public static final RegistryHolder<MountedItemStorageType<?>, ChestMountedStorageType> CHEST;
	public static final RegistryHolder<MountedItemStorageType<?>, DispenserMountedStorageType> DISPENSER;

	// Fluid storage types
	public static final RegistryHolder<MountedFluidStorageType<?>, FluidTankMountedStorageType> FLUID_TANK =
		registerFluid("fluid_tank", FluidTankMountedStorageType::new);

	public static final RegistryHolder<MountedFluidStorageType<?>, CreativeFluidTankMountedStorageType> CREATIVE_FLUID_TANK =
		registerFluid("creative_fluid_tank", CreativeFluidTankMountedStorageType::new);

	static {
		// Register types that also associate with blocks
		SIMPLE = registerItem("simple", SimpleMountedStorageType.Impl::new);
		MountedItemStorageType.REGISTRY.registerProvider(
			SimpleRegistry.Provider.forBlockTag(AllTags.AllBlockTags.SIMPLE_MOUNTED_STORAGE.tag, SIMPLE.get())
		);

		CHEST = registerItem("chest", ChestMountedStorageType::new);
		MountedItemStorageType.REGISTRY.registerProvider(
			SimpleRegistry.Provider.forBlockTag(AllTags.AllBlockTags.CHEST_MOUNTED_STORAGE.tag, CHEST.get())
		);

		DISPENSER = registerItem("dispenser", DispenserMountedStorageType::new);
		MountedItemStorageType.REGISTRY.register(Blocks.DISPENSER, DISPENSER.get());
		MountedItemStorageType.REGISTRY.register(Blocks.DROPPER, DISPENSER.get());
	}

	private static <T extends MountedItemStorageType<?>> RegistryHolder<MountedItemStorageType<?>, T> registerItem(
		String name, Supplier<T> supplier) {
		T instance = supplier.get();
		Registry.register(CreateBuiltInRegistries.MOUNTED_ITEM_STORAGE_TYPE,
			ResourceLocation.fromNamespaceAndPath(Create.ID, name), instance);
		return new RegistryHolder<>(instance);
	}

	private static <T extends MountedFluidStorageType<?>> RegistryHolder<MountedFluidStorageType<?>, T> registerFluid(
		String name, Supplier<T> supplier) {
		T instance = supplier.get();
		Registry.register(CreateBuiltInRegistries.MOUNTED_FLUID_STORAGE_TYPE,
			ResourceLocation.fromNamespaceAndPath(Create.ID, name), instance);
		return new RegistryHolder<>(instance);
	}

	public static void register() {
		// Ensure the class is loaded and registrations occur
	}

	/**
	 * Simple holder that acts like Registrate's RegistryEntry for our purposes.
	 */
	public static class RegistryHolder<R, T extends R> implements Supplier<T> {
		private final T value;

		public RegistryHolder(T value) {
			this.value = value;
		}

		@Override
		public T get() {
			return value;
		}
	}
}
