package com.railwayteam.railways.fabric;

import com.railwayteam.railways.Railways;
import net.fabricmc.api.ModInitializer;

public class RailwaysImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        Railways.init();
    }
}
