package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.config.ConfigBase;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CCConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static CCommon common;
    private static CServer server;

    public static CCommon common() {
        return common;
    }

    public static CServer server() {
        return server;
    }

    public static <T> Supplier<T> safeGetter(Supplier<T> getter, T defaultValue) {
        return () -> {
            try {
                return getter.get();
            } catch (IllegalStateException | NullPointerException ex) {
                // the config is accessed too early (before registration or before config load)
                return defaultValue;
            }
        };
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register() {
        common = register(CCommon::new, ModConfig.Type.COMMON);
        server = register(CServer::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            NeoForgeConfigRegistry.INSTANCE.register(CreateConnected.MODID, pair.getKey(), pair.getValue().specification);

        CStress stress = server().stressValues;
        BlockStressValues.registerProvider(CreateConnected.MODID, new BlockStressValues.IStressValueProvider() {
            @Override public double getImpact(net.minecraft.world.level.block.Block block) {
                java.util.function.DoubleSupplier s = stress.getImpact(block);
                return s != null ? s.getAsDouble() : 0;
            }
            @Override public double getCapacity(net.minecraft.world.level.block.Block block) {
                java.util.function.DoubleSupplier s = stress.getCapacity(block);
                return s != null ? s.getAsDouble() : 0;
            }
            @Override public boolean hasImpact(net.minecraft.world.level.block.Block block) { return stress.getImpact(block) != null; }
            @Override public boolean hasCapacity(net.minecraft.world.level.block.Block block) { return stress.getCapacity(block) != null; }
            @Override public com.simibubi.create.foundation.utility.Couple<Integer> getGeneratedRPM(net.minecraft.world.level.block.Block block) { return null; }
        });

        NeoForgeModConfigEvents.loading(CreateConnected.MODID).register(CCConfigs::onLoad);
        NeoForgeModConfigEvents.reloading(CreateConnected.MODID).register(CCConfigs::onReload);
    }

    public static void onLoad(ModConfig event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getSpec())
                config.onLoad();
    }

    public static void onReload(ModConfig event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getSpec())
                config.onReload();
    }
}
