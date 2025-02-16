package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib_ufo.event.common.TagsUpdatedCallback;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@Shadow
	private ReloadableServerRegistries.Holder fullRegistryHolder;
	
	@Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
	public void port_lib$updateTags(CallbackInfo ci) {
		TagsUpdatedCallback.EVENT.invoker().onTagsUpdated(fullRegistryHolder.get());
	}
}
