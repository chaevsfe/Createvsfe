package com.hlysine.create_connected;

import com.hlysine.create_connected.compat.AdditionalPlacementsCompat;
import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.content.redstonelinkwildcard.LinkWildcardNetworkHandler;
import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.datagen.advancements.CCTriggers;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.slf4j.Logger;

public class CreateConnected implements ModInitializer {
    public static final String MODID = "create_connected";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE
                .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
                .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    @Override
    public void onInitialize() {
        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
        CCSoundEvents.prepare();
        CCSoundEvents.register();
        CCBlocks.register();
        CCItems.register();
        CCBlockEntityTypes.register();
        CCCreativeTabs.register();
        CCPackets.register();
        CCArmInteractionPointTypes.register();
        CCConfigs.register();

        CCInteractionBehaviours.register();
        CCMovementBehaviours.register();
        CCMountedStorageTypes.register();
        CCDisplaySources.register();

        CCItemAttributes.register();
        CCAdvancements.register();
        CCTriggers.register();

        LinkWildcardNetworkHandler.register();

        if (Mods.COPYCATS.isLoaded())
            ServerTickEvents.START_SERVER_TICK.register(server -> CopycatsManager.onLevelTick(server));

        Mods.ADDITIONAL_PLACEMENTS.executeIfInstalled(() -> AdditionalPlacementsCompat::register);

        // Flush all Registrate deferred entries to actual MC registries
        REGISTRATE.register();
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
