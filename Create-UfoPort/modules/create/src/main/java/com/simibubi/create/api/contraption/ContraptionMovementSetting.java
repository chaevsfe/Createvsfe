package com.simibubi.create.api.contraption;

import java.util.Collection;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.registry.SimpleRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * API facade for contraption movement settings. Delegates to the content-layer implementation.
 * <p>
 * Defines whether a block is movable by contraptions.
 * This is used as a fallback check for
 * {@link BlockMovementChecks#isMovementAllowed(BlockState, Level, BlockPos)}.
 */
public enum ContraptionMovementSetting {
	/**
	 * Block is fully movable with no restrictions.
	 */
	MOVABLE,
	/**
	 * Block can be mounted and moved, but if it's on a minecart contraption, the contraption cannot be picked up.
	 */
	NO_PICKUP,
	/**
	 * Block cannot ever be moved by a contraption.
	 */
	UNMOVABLE;

	public static final SimpleRegistry<Block, Supplier<ContraptionMovementSetting>> REGISTRY = SimpleRegistry.create();

	public static void register(ResourceLocation block, Supplier<com.simibubi.create.content.contraptions.ContraptionMovementSetting> settingSupplier) {
		com.simibubi.create.content.contraptions.ContraptionMovementSetting.register(block, settingSupplier);
	}

	public static void register(Block block, Supplier<com.simibubi.create.content.contraptions.ContraptionMovementSetting> settingSupplier) {
		com.simibubi.create.content.contraptions.ContraptionMovementSetting.register(block, settingSupplier);
	}

	/**
	 * Shortcut that gets the block of the given state.
	 */
	@Nullable
	public static ContraptionMovementSetting get(BlockState state) {
		return get(state.getBlock());
	}

	/**
	 * Get the current movement setting of the given block.
	 */
	@Nullable
	public static ContraptionMovementSetting get(Block block) {
		com.simibubi.create.content.contraptions.ContraptionMovementSetting setting =
			com.simibubi.create.content.contraptions.ContraptionMovementSetting.get(block);
		if (setting == null) return null;
		return switch (setting) {
			case MOVABLE -> MOVABLE;
			case NO_PICKUP -> NO_PICKUP;
			case UNMOVABLE -> UNMOVABLE;
		};
	}

	/**
	 * Check if any of the blocks in the collection match the given setting.
	 */
	public static boolean anyAre(Collection<StructureTemplate.StructureBlockInfo> blocks, ContraptionMovementSetting setting) {
		return blocks.stream().anyMatch(b -> get(b.state().getBlock()) == setting);
	}

	/**
	 * Check if any of the blocks in the collection forbid pickup.
	 */
	public static boolean isNoPickup(Collection<StructureTemplate.StructureBlockInfo> blocks) {
		return anyAre(blocks, ContraptionMovementSetting.NO_PICKUP);
	}

	/**
	 * Interface that may optionally be implemented on a Block implementation which will be queried instead of the registry.
	 */
	public interface MovementSettingProvider {
		ContraptionMovementSetting getContraptionMovementSetting();
	}
}
