package com.railwayteam.railways.fabric;

import com.railwayteam.railways.RailwaysClient;
import net.fabricmc.api.ClientModInitializer;

public class RailwaysClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RailwaysClient.init();
    }
}
