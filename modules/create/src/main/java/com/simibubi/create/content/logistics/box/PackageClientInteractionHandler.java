package com.simibubi.create.content.logistics.box;

import io.github.fabricators_of_create.porting_lib_ufo.common.mixin.client.accessor.MinecraftAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * In vanilla, punching an entity doesn't reset the attack timer. This leads to
 * accidentally breaking blocks behind an armor stand or package when punching it
 * in creative mode. This handler resets the miss timer when punching a PackageEntity.
 */
@Environment(EnvType.CLIENT)
public class PackageClientInteractionHandler {

	public static void register() {
		AttackEntityCallback.EVENT.register(PackageClientInteractionHandler::onPlayerPunchPackage);
	}

	private static InteractionResult onPlayerPunchPackage(Player player, Level world, InteractionHand hand,
														  Entity entity, EntityHitResult hitResult) {
		if (!world.isClientSide())
			return InteractionResult.PASS;
		Minecraft mc = Minecraft.getInstance();
		if (player != mc.player)
			return InteractionResult.PASS;
		if (!(entity instanceof PackageEntity))
			return InteractionResult.PASS;
		((MinecraftAccessor) (Object) mc).create$setMissTime(10);
		return InteractionResult.PASS;
	}
}
