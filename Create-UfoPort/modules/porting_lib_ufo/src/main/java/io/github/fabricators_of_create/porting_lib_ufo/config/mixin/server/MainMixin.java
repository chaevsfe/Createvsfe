package io.github.fabricators_of_create.porting_lib_ufo.config.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib_ufo.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib_ufo.config.ConfigType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.Main;

@Mixin(Main.class)
public class MainMixin {
	@Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;startTimerHackThread()V"))
	private static void port_lib$modsLoaded(CallbackInfo ci) {
		ConfigTracker.INSTANCE.loadConfigs(ConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
	}
}
