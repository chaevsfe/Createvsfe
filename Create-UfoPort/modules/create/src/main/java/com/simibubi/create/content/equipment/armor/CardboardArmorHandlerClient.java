package com.simibubi.create.content.equipment.armor;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageRenderer;
import com.simibubi.create.foundation.utility.TickBasedCache;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class CardboardArmorHandlerClient {

	private static final Cache<UUID, Integer> BOXES_PLAYERS_ARE_HIDING_AS = new TickBasedCache<>(20, true);

	/**
	 * Called from Fabric PlayerTickCallback (client-side) to keep cache alive.
	 */
	public static void keepCacheAlive(Player player) {
		if (!CardboardArmorHandler.testForStealth(player))
			return;
		try {
			getCurrentBoxIndex(player);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called from Fabric RenderLivingEntityCallback (registered in ClientEvents)
	 * before rendering a player. Returns true if the default rendering should be cancelled.
	 */
	public static boolean playerRendersAsBoxWhenSneaking(Player player, PoseStack ms,
		net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight, float partialTick) {
		if (!CardboardArmorHandler.testForStealth(player))
			return false;

		if (player == Minecraft.getInstance().player
			&& Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON)
			return true; // cancel default render but don't draw box in first person

		ms.pushPose();

		// We don't have easy access to getRenderOffset in this callback context,
		// so we approximate the bounce offset
		float movement = (float) player.position()
			.subtract(player.xo, player.yo, player.zo)
			.length();

		float interpolatedYaw = Mth.lerp(partialTick, player.yRotO, player.getYRot());

		float scale = player.getScale();
		ms.scale(scale, scale, scale);

		try {
			PartialModel model = AllPartialModels.PACKAGES_TO_HIDE_AS.get(getCurrentBoxIndex(player));
			PackageRenderer.renderBox(player, interpolatedYaw, ms, bufferSource, packedLight, model);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		ms.popPose();
		return true;
	}

	public static void clientTick() {
		// keep alive handled in keepCacheAlive per player
	}

	private static Integer getCurrentBoxIndex(Player player) throws ExecutionException {
		return BOXES_PLAYERS_ARE_HIDING_AS.get(player.getUUID(),
			() -> player.level().random.nextInt(AllPartialModels.PACKAGES_TO_HIDE_AS.size()));
	}

}
