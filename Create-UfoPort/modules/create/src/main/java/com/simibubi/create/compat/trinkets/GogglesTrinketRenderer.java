package com.simibubi.create.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Renders goggles on the player's head when equipped in a Trinkets slot.
 * Mirrors the NeoForge {@code GogglesCurioRenderer} behavior using the Trinkets rendering API.
 */
@Environment(EnvType.CLIENT)
public class GogglesTrinketRenderer implements TrinketRenderer {

	public static void register() {
		TrinketRendererRegistry.registerRenderer(AllItems.GOGGLES.get(), new GogglesTrinketRenderer());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
					   PoseStack matrices, MultiBufferSource multiBufferSource, int light, LivingEntity entity,
					   float limbAngle, float limbDistance, float tickDelta, float animationProgress,
					   float headYaw, float headPitch) {
		if (!AllItems.GOGGLES.isIn(stack))
			return;
		if (!(model instanceof PlayerModel<?> playerModel))
			return;
		if (!(entity instanceof AbstractClientPlayer player))
			return;

		matrices.pushPose();
		TrinketRenderer.followBodyRotations(entity, (PlayerModel<LivingEntity>) (PlayerModel<?>) playerModel);
		TrinketRenderer.translateToFace(matrices, (PlayerModel<AbstractClientPlayer>) (PlayerModel<?>) playerModel, player, headYaw, headPitch);

		// Translate and scale to match head position
		matrices.translate(0, 0, 0.3);
		matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
		matrices.scale(0.625f, 0.625f, 0.625f);

		// If the head armor slot or head/hat trinket slot is occupied, offset the goggles upward
		if (isHeadOccupied(entity)) {
			matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
			matrices.translate(0, -0.25, 0);
		}

		// Render the goggles item model
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer()
			.renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, matrices,
				multiBufferSource, mc.level, 0);
		matrices.popPose();
	}

	/**
	 * Checks whether the entity's head is already occupied by another item (vanilla helmet or trinket hat),
	 * so the goggles can be offset to avoid clipping.
	 */
	private static boolean isHeadOccupied(LivingEntity entity) {
		if (!entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
			return true;

		// Also check the trinkets head/hat slot
		return TrinketsApi.getTrinketComponent(entity)
			.map(component -> {
				Map<String, TrinketInventory> headSlots = component.getInventory().get("head");
				if (headSlots == null)
					return false;
				TrinketInventory hatInv = headSlots.get("hat");
				return hatInv != null && !hatInv.isEmpty();
			})
			.orElse(false);
	}
}
