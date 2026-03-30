package com.mrh0.createaddition;
import com.mrh0.createaddition.event.ClientEventHandler;
import com.mrh0.createaddition.index.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
@Environment(EnvType.CLIENT)
public class CreateAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CAPartials.init();
        CAItemProperties.register();
        ClientEventHandler.register();
    }
}
