package io.github.fabricators_of_create.porting_lib_ufo.entity.mixin.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib_ufo.entity.events.LivingEntityEvents;

@Mixin(MagmaCube.class)
public class MagmaCubeMixin {
	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void onJump(CallbackInfo ci) {
		new LivingEntityEvents.LivingJumpEvent((LivingEntity) (Object) this).sendEvent();
	}
}
