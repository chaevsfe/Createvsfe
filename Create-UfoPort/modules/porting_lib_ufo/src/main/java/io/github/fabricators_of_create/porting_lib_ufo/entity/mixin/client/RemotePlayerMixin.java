package io.github.fabricators_of_create.porting_lib_ufo.entity.mixin.client;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib_ufo.entity.events.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib_ufo.entity.events.LivingEntityEvents;

@Mixin(RemotePlayer.class)
public abstract class RemotePlayerMixin {
	@Inject(method = "hurt", at = @At("HEAD"))
	public void port_lib$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingAttackEvent event = new LivingAttackEvent((LivingEntity) (Object) this, source, amount);
		event.sendEvent();
	}
}
