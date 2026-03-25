package com.simibubi.create.api.registry;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.logistics.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Resource keys for Create's custom registries.
 * Fabric equivalent of NeoForge's CreateRegistries.
 */
public class CreateRegistries {
	public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("item_attribute_type"));

	public static final ResourceKey<Registry<PackagePortTargetType>> PACKAGE_PORT_TARGET_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("package_port_target_type"));

	public static final ResourceKey<Registry<MountedItemStorageType<?>>> MOUNTED_ITEM_STORAGE_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("mounted_item_storage_type"));

	public static final ResourceKey<Registry<MountedFluidStorageType<?>>> MOUNTED_FLUID_STORAGE_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("mounted_fluid_storage_type"));

	public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileRenderMode>>> POTATO_PROJECTILE_RENDER_MODE =
		ResourceKey.createRegistryKey(Create.asResource("potato_projectile/render_mode"));

	public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileEntityHitAction>>> POTATO_PROJECTILE_ENTITY_HIT_ACTION =
		ResourceKey.createRegistryKey(Create.asResource("potato_projectile/entity_hit_action"));

	public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileBlockHitAction>>> POTATO_PROJECTILE_BLOCK_HIT_ACTION =
		ResourceKey.createRegistryKey(Create.asResource("potato_projectile/block_hit_action"));

	public static final ResourceKey<Registry<DisplaySource>> DISPLAY_SOURCE =
		ResourceKey.createRegistryKey(Create.asResource("display_source"));

	public static final ResourceKey<Registry<DisplayTarget>> DISPLAY_TARGET =
		ResourceKey.createRegistryKey(Create.asResource("display_target"));
}
