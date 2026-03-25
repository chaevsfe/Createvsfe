package com.simibubi.create.content.equipment.armor;

import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CardboardArmorHandler {

	/**
	 * Called via EntitySizeCallback (mixin or Fabric API entity size event).
	 * Shrinks hitbox when sneaking in full cardboard armor.
	 */
	public static void playerHitboxChangesWhenHidingAsBox(Entity entity, EntityDimensions original, EntityDimensions[] result) {
		// Fabric: use level() != null instead of NeoForge isAddedToLevel()
		if (entity.level() == null)
			return;
		if (!testForStealth(entity))
			return;

		float scale;
		if (entity instanceof LivingEntity le) {
			scale = le.getScale();
		} else {
			scale = 1.0F;
		}

		result[0] = EntityDimensions.fixed(0.6F * scale, 0.8F * scale).withEyeHeight(0.6F * scale);
		// TODO: Award AllAdvancements.CARDBOARD_ARMOR once it is added to AllAdvancements
	}

	/**
	 * Called from Fabric equipment change event (registered in CommonEvents).
	 */
	public static void playerChangesEquipment(LivingEntity entity, EquipmentSlot slot) {
		if (entity instanceof Player player && player.getPose() == Pose.CROUCHING && (
			isCardboardArmor(player.getItemBySlot(EquipmentSlot.HEAD))
				|| isCardboardArmor(player.getItemBySlot(EquipmentSlot.CHEST))
				|| isCardboardArmor(player.getItemBySlot(EquipmentSlot.LEGS))
				|| isCardboardArmor(player.getItemBySlot(EquipmentSlot.FEET))
		)) {
			// Assuming player is putting on last piece or took off first piece of cardboard armor
			if (!player.level().isClientSide()) {
				Pose pose = player.getPose();
				player.setPose(pose == Pose.CROUCHING ? Pose.STANDING : Pose.CROUCHING);
				player.setPose(pose);
			}
		}
	}

	/**
	 * Called from Fabric LivingEntity visibility callback (registered in CommonEvents).
	 */
	public static float playersStealthWhenWearingCardboard(LivingEntity entity, Entity looker, float currentVisibility) {
		if (!testForStealth(entity))
			return currentVisibility;
		return 0f;
	}

	/**
	 * Called from a server-side tick event (registered in CommonEvents) every entity tick.
	 */
	public static void mobsMayLoseTargetWhenItIsWearingCardboard(Entity entity) {
		if (!(entity instanceof LivingEntity le))
			return;
		if (le.tickCount % 16 != 0)
			return;
		if (!(le instanceof Mob mob))
			return;

		if (testForStealth(mob.getTarget())) {
			mob.setTarget(null);
			// Access widener makes Mob.targetSelector accessible
			GoalSelector selector = mob.targetSelector;
			if (selector != null)
				for (WrappedGoal goal : selector.getAvailableGoals()) {
					if (goal.isRunning() && goal.getGoal() instanceof TargetGoal tg)
						tg.stop();
				}
		}

		if (le instanceof NeutralMob nMob && le.level() instanceof ServerLevel sl) {
			UUID uuid = nMob.getPersistentAngerTarget();
			if (uuid != null && testForStealth(sl.getEntity(uuid)))
				nMob.stopBeingAngry();
		}

		if (testForStealth(mob.getLastHurtByMob())) {
			mob.setLastHurtByMob(null);
			mob.setLastHurtByPlayer(null);
		}
	}

	public static boolean testForStealth(Entity entityIn) {
		if (!(entityIn instanceof LivingEntity entity))
			return false;
		if (entity.getPose() != Pose.CROUCHING)
			return false;
		if (entity instanceof Player player && player.getAbilities().flying)
			return false;
		if (!isCardboardArmor(entity.getItemBySlot(EquipmentSlot.HEAD)))
			return false;
		if (!isCardboardArmor(entity.getItemBySlot(EquipmentSlot.CHEST)))
			return false;
		if (!isCardboardArmor(entity.getItemBySlot(EquipmentSlot.LEGS)))
			return false;
		if (!isCardboardArmor(entity.getItemBySlot(EquipmentSlot.FEET)))
			return false;
		return true;
	}

	public static boolean isCardboardArmor(ItemStack stack) {
		return stack.getItem() instanceof CardboardArmorItem;
	}

}
