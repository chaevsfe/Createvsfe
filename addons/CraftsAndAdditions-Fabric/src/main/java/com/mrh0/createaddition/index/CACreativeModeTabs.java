package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CACreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> MAIN_KEY =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateAddition.asResource("main"));

    public static CreativeModeTab MAIN_TAB_INSTANCE;

    public static void register() {
        MAIN_TAB_INSTANCE = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.createaddition.main"))
            .icon(CABlocks.ELECTRIC_MOTOR::asStack)
            .displayItems((params, output) -> {
                // Add all items registered through CA's Registrate
                for (RegistryEntry<Item> entry : CreateAddition.REGISTRATE.<Item>getAll(Registries.ITEM)) {
                    try {
                        output.accept(new ItemStack(entry.get()));
                    } catch (Exception ignored) {}
                }
            })
            .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN_KEY, MAIN_TAB_INSTANCE);
    }
}
