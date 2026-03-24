package com.simibubi.create.content.logistics.box;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PackageItem extends Item {
	public static final int SLOTS = 9;

	public PackageStyle style;

	public PackageItem(Properties properties, PackageStyle style) {
		super(properties);
		this.style = style;
		PackageStyles.ALL_BOXES.add(this);
		(style.rare() ? PackageStyles.RARE_BOXES : PackageStyles.STANDARD_BOXES).add(this);
	}

	@Override
	public String getDescriptionId() {
		return "item." + Create.ID + (style.rare() ? ".rare_package" : ".package");
	}

	public static boolean isPackage(ItemStack stack) {
		return stack.getItem() instanceof PackageItem;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	// NeoForge hasCustomEntity/createEntity not available on Fabric.
	// Package entity conversion handled via mixin on ItemEntity or entity event.

	public static ItemStack containing(List<ItemStack> stacks) {
		ItemStackHandler newInv = new ItemStackHandler(9);
		int slot = 0;
		for (ItemStack s : stacks) {
			if (!s.isEmpty() && slot < 9) {
				newInv.setStackInSlot(slot++, s.copy());
			}
		}
		return containing(newInv);
	}

	public static ItemStack containing(ItemStackHandler stacks) {
		ItemStack box = PackageStyles.getRandomBox();
		box.set(AllDataComponents.PACKAGE_CONTENTS, ItemHelper.containerContentsFromHandler(stacks));
		return box;
	}

	public static void clearAddress(ItemStack box) {
		box.remove(AllDataComponents.PACKAGE_ADDRESS);
	}

	public static void addAddress(ItemStack box, String address) {
		box.set(AllDataComponents.PACKAGE_ADDRESS, address);
	}

	public static void setOrder(ItemStack box, int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex,
								boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
		PackageOrderData order = new PackageOrderData(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, orderContext);
		box.set(AllDataComponents.PACKAGE_ORDER_DATA, order);
	}

	public static int getOrderId(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).orderId();
		} else {
			return -1;
		}
	}

	public static boolean hasOrderData(ItemStack box) {
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA);
	}

	public static int getIndex(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).fragmentIndex();
		} else {
			return -1;
		}
	}

	public static boolean isFinal(ItemStack box) {
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && box.get(AllDataComponents.PACKAGE_ORDER_DATA).isFinal();
	}

	public static int getLinkIndex(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			return box.get(AllDataComponents.PACKAGE_ORDER_DATA).linkIndex();
		} else {
			return -1;
		}
	}

	public static boolean isFinalLink(ItemStack box) {
		return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && box.get(AllDataComponents.PACKAGE_ORDER_DATA).isFinalLink();
	}

	@Nullable
	public static PackageOrderWithCrafts getOrderContext(ItemStack box) {
		if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
			PackageOrderData data = box.get(AllDataComponents.PACKAGE_ORDER_DATA);
			return data.orderContext();
		} else if (box.has(AllDataComponents.PACKAGE_ORDER_CONTEXT)) {
			return box.get(AllDataComponents.PACKAGE_ORDER_CONTEXT);
		} else {
			return null;
		}
	}

	public static void addOrderContext(ItemStack box, PackageOrderWithCrafts orderContext) {
		box.set(AllDataComponents.PACKAGE_ORDER_CONTEXT, orderContext);
	}

	public static boolean matchAddress(ItemStack box, String address) {
		return matchAddress(getAddress(box), address);
	}

	public static boolean matchAddress(String boxAddress, String address) {
		if (address.isBlank())
			return boxAddress.isBlank();
		if (address.equals("*") || boxAddress.equals("*"))
			return true;
		if (address.equals(boxAddress))
			return true;
		// Simple glob matching — converts glob to regex
		return address.matches(toGlobRegex(boxAddress)) ||
			boxAddress.matches(toGlobRegex(address));
	}

	/** Simple glob→regex: * matches any sequence, ? matches single char */
	private static String toGlobRegex(String glob) {
		StringBuilder sb = new StringBuilder("^");
		for (char c : glob.toCharArray()) {
			switch (c) {
				case '*' -> sb.append(".*");
				case '?' -> sb.append(".");
				case '.' -> sb.append("\\.");
				case '\\' -> sb.append("\\\\");
				case '[', ']', '(', ')', '{', '}', '^', '$', '+', '|' -> sb.append("\\").append(c);
				default -> sb.append(c);
			}
		}
		sb.append("$");
		return sb.toString();
	}

	public static String getAddress(ItemStack box) {
		return box.getOrDefault(AllDataComponents.PACKAGE_ADDRESS, "");
	}

	public static float getWidth(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi)
			return pi.style.width() / 16f;
		return 1;
	}

	public static float getHeight(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi)
			return pi.style.height() / 16f;
		return 1;
	}

	public static float getHookDistance(ItemStack box) {
		if (box.getItem() instanceof PackageItem pi)
			return pi.style.riggingOffset() / 16f;
		return 1;
	}

	public static ItemStackHandler getContents(ItemStack box) {
		ItemStackHandler newInv = new ItemStackHandler(9);
		ItemContainerContents contents = box.getOrDefault(AllDataComponents.PACKAGE_CONTENTS, ItemContainerContents.EMPTY);
		ItemHelper.fillItemStackHandler(contents, newInv);
		return newInv;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltipComponents,
								TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);

		if (stack.has(AllDataComponents.PACKAGE_ADDRESS))
			tooltipComponents.add(Component.literal("\u2192 " + stack.get(AllDataComponents.PACKAGE_ADDRESS))
				.withStyle(ChatFormatting.GOLD));

		if (!stack.has(AllDataComponents.PACKAGE_CONTENTS))
			return;

		int visibleNames = 0;
		int skippedNames = 0;
		ItemStackHandler contents = getContents(stack);
		for (int i = 0; i < contents.getSlotCount(); i++) {
			ItemStack itemstack = contents.getStackInSlot(i);
			if (itemstack.isEmpty())
				continue;
			if (itemstack.getItem() instanceof SpawnEggItem)
				continue;
			if (visibleNames > 2) {
				skippedNames++;
				continue;
			}

			visibleNames++;
			tooltipComponents.add(itemstack.getHoverName()
				.copy()
				.append(" x")
				.append(String.valueOf(itemstack.getCount()))
				.withStyle(ChatFormatting.GRAY));
		}

		if (skippedNames > 0)
			tooltipComponents.add(Component.translatable("container.shulkerBox.more", skippedNames)
				.withStyle(ChatFormatting.ITALIC));
	}

	// Throwing mechanics

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.BOW;
	}

	public InteractionResultHolder<ItemStack> open(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack box = playerIn.getItemInHand(handIn);
		ItemStackHandler contents = getContents(box);
		ItemStack particle = box.copy();

		playerIn.setItemInHand(handIn, box.getCount() <= 1 ? ItemStack.EMPTY : box.copyWithCount(box.getCount() - 1));

		if (!worldIn.isClientSide()) {
			for (int i = 0; i < contents.getSlotCount(); i++) {
				ItemStack itemstack = contents.getStackInSlot(i);
				if (itemstack.isEmpty())
					continue;

				if (itemstack.getItem() instanceof SpawnEggItem sei && worldIn instanceof ServerLevel sl) {
					EntityType<?> entitytype = sei.getType(itemstack);
					Entity entity = entitytype.spawn(sl, itemstack, null, BlockPos.containing(playerIn.position()
							.add(playerIn.getLookAngle()
								.multiply(1, 0, 1)
								.normalize())),
						MobSpawnType.SPAWN_EGG, false, false);
					if (entity != null)
						itemstack.shrink(1);
				}

				playerIn.getInventory()
					.placeItemBackInInventory(itemstack.copy());
			}
		}

		Vec3 position = playerIn.position();
		AllSoundEvents.PACKAGE_POP.playOnServer(worldIn, playerIn.blockPosition());

		if (worldIn.isClientSide()) {
			for (int i = 0; i < 10; i++) {
				Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, worldIn.getRandom(), .125f);
				Vec3 pos = position.add(0, 0.5, 0)
					.add(playerIn.getLookAngle()
						.scale(.5))
					.add(motion.scale(4));
				worldIn.addParticle(new ItemParticleOption(ParticleTypes.ITEM, particle), pos.x, pos.y, pos.z, motion.x,
					motion.y, motion.z);
			}
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, box);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isShiftKeyDown()) {
			return open(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
		}

		// Package entity placement deferred — needs PackageEntity ported first
		return super.useOn(context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (player.isShiftKeyDown())
			return open(world, player, hand);
		ItemStack itemstack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		return InteractionResultHolder.success(itemstack);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticks) {
		if (!(entity instanceof Player player))
			return;
		int i = this.getUseDuration(stack, entity) - ticks;
		if (i < 0)
			return;

		float f = getPackageVelocity(i);
		if (f < 0.1D)
			return;
		if (world.isClientSide)
			return;

		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
			SoundSource.NEUTRAL, 0.5F, 0.5F);

		// Package entity throwing deferred — needs PackageEntity ported first
		ItemStack copy = stack.copy();
		if (!player.getAbilities().instabuild)
			stack.shrink(1);
	}

	public static float getPackageVelocity(int chargeTime) {
		float f = (float) chargeTime / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F)
			f = 1.0F;
		return f;
	}

	// ---- PackageOrderData record ----

	public record PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex,
								   boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
		public PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex,
								boolean isFinal, Optional<PackageOrderWithCrafts> orderContext) {
			this(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, orderContext.orElse(null));
		}

		public static final Codec<PackageOrderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("order_id").forGetter(PackageOrderData::orderId),
			Codec.INT.fieldOf("link_index").forGetter(PackageOrderData::linkIndex),
			Codec.BOOL.fieldOf("is_final_link").forGetter(PackageOrderData::isFinalLink),
			Codec.INT.fieldOf("fragment_index").forGetter(PackageOrderData::fragmentIndex),
			Codec.BOOL.fieldOf("is_final").forGetter(PackageOrderData::isFinal),
			PackageOrderWithCrafts.CODEC.optionalFieldOf("order_context").forGetter(i -> Optional.ofNullable(i.orderContext))
		).apply(instance, PackageOrderData::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, PackageOrderData::orderId,
			ByteBufCodecs.INT, PackageOrderData::linkIndex,
			ByteBufCodecs.BOOL, PackageOrderData::isFinalLink,
			ByteBufCodecs.INT, PackageOrderData::fragmentIndex,
			ByteBufCodecs.BOOL, PackageOrderData::isFinal,
			CreateStreamCodecs.nullable(PackageOrderWithCrafts.STREAM_CODEC), PackageOrderData::orderContext,
			PackageOrderData::new
		);
	}
}
