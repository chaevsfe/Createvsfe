package io.github.fabricators_of_create.porting_lib_ufo.entity;

import io.github.fabricators_of_create.porting_lib_ufo.entity.events.LivingDeathEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntityHooks {
	
	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		var event = new LivingDeathEvent(entity, src);
		event.sendEvent();
		return event.isCanceled();
	}
	
}
