package io.github.fabricators_of_create.porting_lib_ufo.entity.mixin.common;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib_ufo.entity.events.ProjectileImpactCallback;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
	@Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
	private void port_lib$onProjectileHit(HitResult result, CallbackInfo ci) {
		if (ProjectileImpactCallback.EVENT.invoker().onImpact((Projectile) (Object) this, result)) {
			ci.cancel();
		}
	}
}
