package systems.alexander.bellsandwhistles.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import systems.alexander.bellsandwhistles.BellsAndWhistles;
import systems.alexander.bellsandwhistles.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> BELLS_AND_WHISTLES_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(BellsAndWhistles.MOD_ID, "bells_and_whistles_tab")
    );

    public static final CreativeModeTab BELLS_AND_WHISTLES_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.METAL_PILOT))
            .title(Component.translatable("creativetab.bellsandwhistlestab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModBlocks.ANDESITE_BOGIE_STEPS);
                pOutput.accept(ModBlocks.BRASS_BOGIE_STEPS);
                pOutput.accept(ModBlocks.COPPER_BOGIE_STEPS);
                pOutput.accept(ModBlocks.ANDESITE_GRAB_RAILS);
                pOutput.accept(ModBlocks.BRASS_GRAB_RAILS);
                pOutput.accept(ModBlocks.COPPER_GRAB_RAILS);
                pOutput.accept(ModBlocks.ANDESITE_DOOR_STEP);
                pOutput.accept(ModBlocks.BRASS_DOOR_STEP);
                pOutput.accept(ModBlocks.COPPER_DOOR_STEP);
                pOutput.accept(ModBlocks.ORNATE_IRON_TRAPDOOR);
                pOutput.accept(ModBlocks.HEADLIGHT);
                pOutput.accept(ModBlocks.STATION_PLATFORM);
                pOutput.accept(ModBlocks.METRO_CASING);
                pOutput.accept(ModBlocks.CORRUGATED_METRO_CASING);
                pOutput.accept(ModBlocks.METRO_PANEL);
                pOutput.accept(ModBlocks.CORRUGATED_METRO_PANEL);
                pOutput.accept(ModBlocks.METRO_TRAPDOOR);
                pOutput.accept(ModBlocks.METRO_WINDOW);

                pOutput.accept(ModBlocks.ANDESITE_PILOT);
                pOutput.accept(ModBlocks.BRASS_PILOT);
                pOutput.accept(ModBlocks.COPPER_PILOT);
                pOutput.accept(ModBlocks.METAL_PILOT);
                pOutput.accept(ModBlocks.POLISHED_ANDESITE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_GRANITE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_DIORITE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_DEEPSLATE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_DRIPSTONE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_TUFF_PILOT);
                pOutput.accept(ModBlocks.POLISHED_CALCITE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_LIMESTONE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_SCORIA_PILOT);
                pOutput.accept(ModBlocks.POLISHED_SCORCHIA_PILOT);
                pOutput.accept(ModBlocks.POLISHED_CRIMSITE_PILOT);
                pOutput.accept(ModBlocks.POLISHED_OCHRUM_PILOT);
                pOutput.accept(ModBlocks.POLISHED_VERIDIUM_PILOT);
                pOutput.accept(ModBlocks.POLISHED_ASURINE_PILOT);
            })
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, BELLS_AND_WHISTLES_TAB_KEY, BELLS_AND_WHISTLES_TAB);
    }
}
