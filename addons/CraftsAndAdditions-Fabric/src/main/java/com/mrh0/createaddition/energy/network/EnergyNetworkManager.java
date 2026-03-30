package com.mrh0.createaddition.energy.network;
import net.minecraft.world.level.LevelAccessor;
import java.util.HashMap;
import java.util.Map;
public class EnergyNetworkManager {
    public static Map<LevelAccessor, EnergyNetworkManager> instances = new HashMap<>();
    public EnergyNetworkManager(LevelAccessor level) { instances.put(level, this); }
    public static void tickWorld(LevelAccessor level) {}
}
