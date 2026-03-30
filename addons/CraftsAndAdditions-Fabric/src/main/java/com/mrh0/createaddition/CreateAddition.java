package com.mrh0.createaddition;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.fabric.EnergyLookup;
import com.mrh0.createaddition.event.GameEvents;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class CreateAddition implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "createaddition";
    public static boolean IE_ACTIVE = false, CC_ACTIVE = false, AE2_ACTIVE = false;
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    static { REGISTRATE.setTooltipModifierFactory(item -> TooltipModifier.mapNull(KineticStats.create(item))); }
    @Override
    public void onInitialize() {
        Config.init();
        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("ae2");
        CACreativeModeTabs.register();
        CABlocks.register();
        CABlockEntities.register();
        CAItems.register();
        CAFluids.register();
        CAEffects.register();
        CARecipes.register();
        CASounds.register();
        CADamageTypes.register();
        CADisplaySources.register();
        CAArmInteractions.register();
        GameEvents.register();
        EnergyLookup.ENERGY.registerFallback((world, pos, state, be, direction) -> {
            if (be instanceof com.mrh0.createaddition.energy.BaseElectricBlockEntity electricBe) return electricBe.getEnergyStorage(direction);
            return null;
        });
        // Flush all Registrate deferred entries to actual MC registries
        REGISTRATE.register();
        LOGGER.info("Create Crafts & Additions Initialized!");
    }
    public static ResourceLocation asResource(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }
}
