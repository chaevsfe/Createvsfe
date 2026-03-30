package com.mrh0.createaddition.event;

import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.mrh0.createaddition.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientEventHandler {

    public static boolean clientRenderHeldWire = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ClientMinecraftWrapper.getPlayer() == null) return;
            ItemStack stack = ClientMinecraftWrapper.getPlayer().getInventory().getSelected();
            if (stack.isEmpty()) return;
            if (WireSpool.isRemover(stack.getItem())) return;
            clientRenderHeldWire = Util.getWireNodeOfSpools(stack) != null;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CASoundScapes.tick();
        });

        // Register reload listener
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListener());

        // Register game event client handler
        GameEvents.registerClient();
    }
}
