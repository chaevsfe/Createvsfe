package com.hlysine.create_connected.mixin.featuretoggle;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CCConfigs;
import com.simibubi.create.foundation.config.ui.ConfigScreen;
import com.simibubi.create.foundation.config.ui.SubMenuConfigScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubMenuConfigScreen.class, remap = false)
public class SubMenuConfigScreenMixin {
    @Inject(
            method = "saveChanges()V",
            at = @At("TAIL")
    )
    private void saveChangesAndRefresh(CallbackInfo ci) {
        if (ConfigScreen.modID != null && ConfigScreen.modID.equals(CreateConnected.MODID)) {
            // On Fabric, this mixin only applies on the client side (client-only class)
            // Sync if we're in a singleplayer world (client is also server) or if running as server
            boolean isIntegratedServer = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                    && Minecraft.getInstance().hasSingleplayerServer();
            boolean isDedicatedServer = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
            if (isIntegratedServer || isDedicatedServer) {
                CCConfigs.common().syncToAllPlayers();
            }
        }
    }
}
