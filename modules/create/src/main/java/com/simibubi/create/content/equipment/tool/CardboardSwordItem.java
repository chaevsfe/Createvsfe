package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;

import io.github.fabricators_of_create.porting_lib_ufo.enchant.CustomEnchantingBehaviorItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class CardboardSwordItem extends SwordItem implements CustomEnchantingBehaviorItem {

	public CardboardSwordItem(Properties pProperties) {
		super(AllToolMaterials.CARDBOARD, pProperties);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		// Cardboard swords should not gain random enchantments at the enchanting table.
		// Only Knockback via anvil is intended (handled by isBookEnchantable).
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		ItemEnchantments enchants = book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Holder<Enchantment> enchantment : enchants.keySet()) {
			if (!enchantment.is(Enchantments.KNOCKBACK))
				return false;
		}
		return true;
	}

	/**
	 * Called from Fabric AttackEntityCallback registered in CommonEvents.
	 * Replicates knockback behaviour without hurting the target.
	 */
	public static boolean onAttackEntity(Player attacker, LivingEntity target) {
		if (target.getType().is(EntityTypeTags.ARTHROPOD))
			return false;
		ItemStack stack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
		if (!AllItems.CARDBOARD_SWORD.isIn(stack))
			return false;

		AllSoundEvents.CARDBOARD_SWORD.playFrom(attacker, 0.75f, 1.85f);

		// Replicate knockback behaviour without hurting the target
		float knockbackStrength = (float) (attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 2);
		if (attacker.level() instanceof ServerLevel serverLevel)
			knockbackStrength = EnchantmentHelper.modifyKnockback(serverLevel, stack, target, serverLevel.damageSources().playerAttack(attacker), knockbackStrength);
		if (attacker.isSprinting() && attacker.getAttackStrengthScale(0.5f) > 0.9f)
			++knockbackStrength;

		if (knockbackStrength > 0) {
			float yRot = attacker.getYRot();
			knockback(target, knockbackStrength, yRot);

			MobCategory targetType = target.getType().getCategory();
			boolean targetIsPlayer = target instanceof Player;

			if (target instanceof ServerPlayer sp) {
				// Send knockback packet to the server player (handled client-side)
				KnockbackPacket.sendTo(sp, yRot, knockbackStrength);
			}

			if ((targetType == MobCategory.MISC || targetType == MobCategory.CREATURE) && !targetIsPlayer)
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 9, true, false, false));

			attacker.setDeltaMovement(attacker.getDeltaMovement()
				.multiply(0.6D, 1.0D, 0.6D));
			attacker.setSprinting(false);
		}

		return true;
	}

	/**
	 * Called from Fabric AttackBlockCallback registered in CommonEvents/ClientEvents.
	 */
	public static void onClickBlock(Player player, boolean isClientSide) {
		ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!AllItems.CARDBOARD_SWORD.isIn(itemStack))
			return;
		// Play sound — only one side to avoid double-play
		if (!isClientSide)
			AllSoundEvents.CARDBOARD_SWORD.playFrom(player, 0.5f, 1.85f);
	}

	public static void knockback(LivingEntity target, double knockbackStrength, float yRot) {
		target.stopRiding();
		target.knockback(knockbackStrength * 0.5F, Mth.sin(yRot * Mth.DEG_TO_RAD), -Mth.cos(yRot * Mth.DEG_TO_RAD));
	}
}
