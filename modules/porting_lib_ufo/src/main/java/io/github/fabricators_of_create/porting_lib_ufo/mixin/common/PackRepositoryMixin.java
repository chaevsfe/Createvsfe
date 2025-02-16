package io.github.fabricators_of_create.porting_lib_ufo.mixin.common;

import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib_ufo.event.common.AddPackFindersCallback;

import java.util.Set;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$addModdedPacks(RepositorySource[] repositorySources, CallbackInfo ci) {
		AddPackFindersCallback.EVENT.invoker().addPack(sources::add);
	}
}
