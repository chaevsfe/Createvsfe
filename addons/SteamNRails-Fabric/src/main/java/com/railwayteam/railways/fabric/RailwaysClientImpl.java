package com.railwayteam.railways.fabric;

import com.railwayteam.railways.RailwaysClient;
import com.railwayteam.railways.registry.fabric.CRBlockEntityVisuals;
import net.fabricmc.api.ClientModInitializer;

public class RailwaysClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RailwaysClient.init();
        CRBlockEntityVisuals.register();
    }
}
