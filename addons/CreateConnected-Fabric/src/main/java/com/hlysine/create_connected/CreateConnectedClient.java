package com.hlysine.create_connected;

import net.fabricmc.api.ClientModInitializer;

public class CreateConnectedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CCPartialModels.register();
        CCPonderPlugin.register();
    }
}
