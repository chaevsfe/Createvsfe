package com.simibubi.create.api.behaviour.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayBoardTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// TODO: 1.21.1+ - Move into api package
public abstract class DisplaySource {
	public static final SimpleRegistry.Multi<Block, DisplaySource> BY_BLOCK = SimpleRegistry.Multi.create();
	public static final SimpleRegistry.Multi<BlockEntityType<?>, DisplaySource> BY_BLOCK_ENTITY = SimpleRegistry.Multi.create();

	public static final List<MutableComponent> EMPTY = ImmutableList.of(Component.empty());
	public static final MutableComponent EMPTY_LINE = Component.empty();
	public static final MutableComponent WHITESPACE = CommonComponents.space();

	public abstract List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats);

	public void transferData(DisplayLinkContext context, DisplayTarget activeTarget, int line) {
		DisplayTargetStats stats = activeTarget.provideStats(context);

		if (activeTarget instanceof DisplayBoardTarget fddt) {
			List<List<MutableComponent>> flapDisplayText = provideFlapDisplayText(context, stats);
			fddt.acceptFlapText(line, flapDisplayText, context);
		}

		List<MutableComponent> text = provideText(context, stats);
		if (text.isEmpty())
			text = EMPTY;

		if (activeTarget.requiresComponentSanitization())
			for (MutableComponent component : text)
				if (component.getStyle() != null && component.getStyle().getClickEvent() != null)
					return; // Naughty

		activeTarget.acceptText(line, text, context);
	}

	public void onSignalReset(DisplayLinkContext context) {
	}

	public void populateData(DisplayLinkContext context) {
	}

	public int getPassiveRefreshTicks() {
		return 100;
	}

	public boolean shouldPassiveReset() {
		return true;
	}

	protected final ResourceLocation getId() {
		return CreateBuiltInRegistries.DISPLAY_SOURCE.getKey(this);
	}

	protected String getTranslationKey() {
		return this.getId().getPath();
	}

	public Component getName() {
		return Component.translatable(this.getId().getNamespace() + ".display_source." + getTranslationKey());
	}

	public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout, int lineIndex) {
		loadFlapDisplayLayout(context, flapDisplay, layout);
	}

	public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
									  FlapDisplayLayout layout) {
		if (!layout.isLayout("Default"))
			layout.loadDefault(flapDisplay.getMaxCharCount());
	}

	public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
		return provideText(context, stats).stream()
			.map(Arrays::asList)
			.toList();
	}

	@Environment(EnvType.CLIENT)
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
										 boolean isFirstLine) {
	}

	/**
	 * Get the DisplaySource with the given ID.
	 */
	@Nullable
	public static DisplaySource get(@Nullable ResourceLocation id) {
		if (id == null)
			return null;
		return CreateBuiltInRegistries.DISPLAY_SOURCE.get(id);
	}

	/**
	 * Get all DisplaySources applicable to the block at the given location, checking both the Block and BlockEntity.
	 * Returns an empty list if none are present, not null.
	 */
	public static List<DisplaySource> getAll(LevelAccessor level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		List<DisplaySource> byBlock = BY_BLOCK.get(state);

		BlockEntity be = level.getBlockEntity(pos);
		if (be == null)
			return byBlock;

		List<DisplaySource> byBe = BY_BLOCK_ENTITY.get(be.getType());

		if (byBlock.isEmpty()) {
			if (byBe.isEmpty()) {
				// none
				return List.of();
			} else {
				// only BlockEntity
				return byBe;
			}
		} else if (byBe.isEmpty()) {
			// only Block
			return byBlock;
		} else {
			// both present, combine
			List<DisplaySource> combined = new ArrayList<>(byBlock);
			combined.addAll(byBe);
			return combined;
		}
	}
}
