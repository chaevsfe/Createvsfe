package com.simibubi.create.api.behaviour.display;

import java.util.List;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DisplayTarget {
	public static final SimpleRegistry<Block, DisplayTarget> BY_BLOCK = SimpleRegistry.create();
	public static final SimpleRegistry<BlockEntityType<?>, DisplayTarget> BY_BLOCK_ENTITY = SimpleRegistry.create();

	/**
	 * Fabric replacement for Forge's BlockEntity.getPersistentData().
	 * Stores extra non-serialized per-BlockEntity data in memory.
	 */
	private static final WeakHashMap<BlockEntity, CompoundTag> PERSISTENT_DATA = new WeakHashMap<>();

	private static CompoundTag getPersistentData(BlockEntity be) {
		return PERSISTENT_DATA.computeIfAbsent(be, k -> new CompoundTag());
	}

	public abstract void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context);

	public abstract DisplayTargetStats provideStats(DisplayLinkContext context);

	public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
		VoxelShape shape = level.getBlockState(pos)
			.getShape(level, pos);
		if (shape.isEmpty())
			return new AABB(pos);
		return shape.bounds()
			.move(pos);
	}

	public Component getLineOptionText(int line) {
		return Lang.translateDirect("display_target.line", line + 1);
	}

	public static void reserve(int line, BlockEntity target, DisplayLinkContext context) {
		if (line == 0)
			return;

		CompoundTag tag = getPersistentData(target);
		CompoundTag compound = tag.getCompound("DisplayLink");
		compound.putLong("Line" + line, context.blockEntity()
			.getBlockPos()
			.asLong());
		tag.put("DisplayLink", compound);
	}

	public boolean isReserved(int line, BlockEntity target, DisplayLinkContext context) {
		CompoundTag tag = getPersistentData(target);
		CompoundTag compound = tag.getCompound("DisplayLink");

		if (!compound.contains("Line" + line))
			return false;

		long l = compound.getLong("Line" + line);
		BlockPos reserved = BlockPos.of(l);

		if (!reserved.equals(context.blockEntity()
			.getBlockPos()) && AllBlocks.DISPLAY_LINK.has(target.getLevel()
				.getBlockState(reserved)))
			return true;

		compound.remove("Line" + line);
		if (compound.isEmpty())
			tag.remove("DisplayLink");
		return false;
	}

	public boolean requiresComponentSanitization() {
		return false;
	}

	/**
	 * Get the DisplayTarget with the given ID.
	 */
	@Nullable
	public static DisplayTarget get(@Nullable ResourceLocation id) {
		if (id == null)
			return null;
		return CreateBuiltInRegistries.DISPLAY_TARGET.get(id);
	}

	/**
	 * Get the DisplayTarget applicable to the given location, or null if there isn't one.
	 */
	@Nullable
	public static DisplayTarget get(LevelAccessor level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		DisplayTarget byBlock = BY_BLOCK.get(state);
		// block takes priority if present, it's more granular
		if (byBlock != null)
			return byBlock;

		BlockEntity be = level.getBlockEntity(pos);
		if (be == null)
			return null;

		DisplayTarget byBe = BY_BLOCK_ENTITY.get(be.getType());
		if (byBe != null)
			return byBe;

		return null;
	}
}
