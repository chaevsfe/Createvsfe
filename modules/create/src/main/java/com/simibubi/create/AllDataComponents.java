package com.simibubi.create;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyData;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity.SchematicannonOptions;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackPlacement.ConnectingFrom;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * All data component types registered by Create.
 *
 * <p>Most components use proper typed codecs. Remaining CompoundTag-based components
 * (legacy items like MINECART_CONTRAPTION, SYM_WAND, TOOLBOX, ZAPPER,
 * SEQUENCED_ASSEMBLY, CLIPBOARD_CONTENT, ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES) will be
 * converted as their supporting types are ported from NeoForge.</p>
 */
public class AllDataComponents {

	// ============================================================
	// Existing components (preserved from UfoPort for compatibility)
	// ============================================================

	// Contraptions
	public static DataComponentType<CompoundTag> MINECART_CONTRAPTION = null;

	// Clipboard
	public static DataComponentType<CompoundTag> CLIPBOARD_EDITING = null;

	// Blueprint
	public static DataComponentType<CompoundTag> BLUEPRINT_DATA = null;

	// Filters (CompoundTag for backwards compat — NeoForge uses ItemContainerContents)
	public static DataComponentType<CompoundTag> FILTER_DATA = null;

	// Sand Paper — stores the ItemStack being polished
	public static DataComponentType<ItemStack> POLISHING = null;

	// Symmetry Wand (CompoundTag for backwards compat — NeoForge uses SymmetryMirror)
	public static DataComponentType<CompoundTag> SYM_WAND = null;

	// Toolbox (CompoundTag for backwards compat — NeoForge uses ToolboxInventory)
	public static DataComponentType<CompoundTag> TOOLBOX = null;

	// Zapper (CompoundTag for backwards compat — NeoForge splits into multiple typed components)
	public static DataComponentType<CompoundTag> ZAPPER = null;

	// Potion fluid bottle type
	public static DataComponentType<BottleType> BOTTLE_TYPE = null;

	// Sequenced Assembly — tracks recipe id, step, and progress on transitional items
	public static DataComponentType<SequencedAssemblyData> SEQUENCED_ASSEMBLY = null;

	// Schematic (CompoundTag for backwards compat — NeoForge splits into multiple typed components)
	public static DataComponentType<CompoundTag> SCHEMATIC_DATA = null;

	// Schedule
	public static DataComponentType<CompoundTag> SCHEDULE_DATA = null;

	// Track item
	public static DataComponentType<CompoundTag> TRACK_ITEM = null;

	// Track targeting
	public static DataComponentType<CompoundTag> TRACK_TARGETING = null;

	// Display link
	public static DataComponentType<BlockPos> DISPLAY_LINK_POS = null;

	// Belt first shaft / pulley
	public static DataComponentType<BlockPos> FIRST_PULLEY = null;

	// Backtank air
	public static DataComponentType<Integer> AIR_TANK = null;

	// Recipe inference flag
	public static DataComponentType<Boolean> INFERRED_FROM_RECIPE = null;

	// Chromatic compound light collection
	public static DataComponentType<Integer> COLLECTING_LIGHT = null;

	// ============================================================
	// New components ported from NeoForge 6.0.9
	// ============================================================

	// --- Zapper / Shaper components (NeoForge splits ZAPPER CompoundTag into individual typed components) ---

	/** Zapper placement pattern */
	public static DataComponentType<PlacementPatterns> PLACEMENT_PATTERN = null;

	/** Shaper brush type */
	public static DataComponentType<TerrainBrushes> SHAPER_BRUSH = null;

	/** Shaper brush size parameters */
	public static DataComponentType<BlockPos> SHAPER_BRUSH_PARAMS = null;

	/** Shaper placement options */
	public static DataComponentType<PlacementOptions> SHAPER_PLACEMENT_OPTIONS = null;

	/** Shaper tool type */
	public static DataComponentType<TerrainTools> SHAPER_TOOL = null;

	/** Block used by shaper */
	public static DataComponentType<BlockState> SHAPER_BLOCK_USED = null;

	/** Whether shaper should swap mode */
	public static DataComponentType<Boolean> SHAPER_SWAP = null;

	/** Additional block data for shaper */
	public static DataComponentType<CompoundTag> SHAPER_BLOCK_DATA = null;

	// --- Filter components (NeoForge splits FILTER_DATA into individual typed components) ---

	/** Filter item contents */
	public static DataComponentType<ItemContainerContents> FILTER_ITEMS = null;

	/** Whether filter items should respect NBT/components */
	public static DataComponentType<Boolean> FILTER_ITEMS_RESPECT_NBT = null;

	/** Whether filter is in blacklist mode */
	public static DataComponentType<Boolean> FILTER_ITEMS_BLACKLIST = null;

	/** Attribute filter whitelist mode */
	public static DataComponentType<AttributeFilterWhitelistMode> ATTRIBUTE_FILTER_WHITELIST_MODE = null;

	/** Attribute filter matched attributes list */
	public static DataComponentType<List<ItemAttribute.ItemAttributeEntry>> ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES = null;

	// --- Clipboard components ---

	/** New clipboard content format. NeoForge type: ClipboardContent */
	public static DataComponentType<CompoundTag> CLIPBOARD_CONTENT = null;

	// --- Track components (NeoForge splits TRACK_ITEM/TRACK_TARGETING into individual typed components) ---

	/** Track connection start data */
	public static DataComponentType<ConnectingFrom> TRACK_CONNECTING_FROM = null;

	/** Whether track should use extended curve */
	public static DataComponentType<Boolean> TRACK_EXTENDED_CURVE = null;

	/** Track targeting selected position */
	public static DataComponentType<BlockPos> TRACK_TARGETING_ITEM_SELECTED_POS = null;

	/** Track targeting selected direction (true = front, false = back) */
	public static DataComponentType<Boolean> TRACK_TARGETING_ITEM_SELECTED_DIRECTION = null;

	/** Track targeting bezier location */
	public static DataComponentType<BezierTrackPointLocation> TRACK_TARGETING_ITEM_BEZIER = null;

	// --- Schematic components (NeoForge splits SCHEMATIC_DATA into individual typed components) ---

	/** Whether schematic is deployed */
	public static DataComponentType<Boolean> SCHEMATIC_DEPLOYED = null;

	/** Schematic owner username */
	public static DataComponentType<String> SCHEMATIC_OWNER = null;

	/** Schematic file name */
	public static DataComponentType<String> SCHEMATIC_FILE = null;

	/** Schematic placement anchor position */
	public static DataComponentType<BlockPos> SCHEMATIC_ANCHOR = null;

	/** Schematic rotation */
	public static DataComponentType<Rotation> SCHEMATIC_ROTATION = null;

	/** Schematic mirror */
	public static DataComponentType<Mirror> SCHEMATIC_MIRROR = null;

	/** Schematic structure bounds */
	public static DataComponentType<Vec3i> SCHEMATIC_BOUNDS = null;

	/** Schematic content hash for validation */
	public static DataComponentType<Integer> SCHEMATIC_HASH = null;

	// --- Sand Paper ---

	/** Flag for JEI/REI display of sand paper polishing (no data, just presence) */
	public static DataComponentType<Boolean> SAND_PAPER_JEI = null;

	// --- Linked Controller ---

	/** Linked controller frequency items */
	public static DataComponentType<ItemContainerContents> LINKED_CONTROLLER_ITEMS = null;

	// --- Toolbox ---

	/** UUID identifying which toolbox an item came from */
	public static DataComponentType<UUID> TOOLBOX_UUID = null;

	// --- Symmetry Wand (NeoForge splits SYM_WAND into individual typed components) ---

	/** Whether symmetry wand is enabled */
	public static DataComponentType<Boolean> SYMMETRY_WAND_ENABLE = null;

	/** Whether symmetry wand shows simulation overlay */
	public static DataComponentType<Boolean> SYMMETRY_WAND_SIMULATE = null;

	// --- Schematicannon ---

	/** Schematicannon placement options */
	public static DataComponentType<SchematicannonOptions> SCHEMATICANNON_OPTIONS = null;

	// ============================================================
	// Registration
	// ============================================================

	public static void register() {
		// --- Existing components (preserved from UfoPort) ---
		MINECART_CONTRAPTION = registerCompoundTag("minecart_contraption");
		BLUEPRINT_DATA = registerCompoundTag("blueprint_data");
		FILTER_DATA = registerCompoundTag("filter_data");
		CLIPBOARD_EDITING = registerCompoundTag("clipboard_editing");
		POLISHING = register("polishing",
			builder -> builder.persistent(ItemStack.CODEC).networkSynchronized(ItemStack.STREAM_CODEC));
		SYM_WAND = registerCompoundTag("symmetry_wand");
		TOOLBOX = registerCompoundTag("toolbox");
		ZAPPER = registerCompoundTag("zapper");
		BOTTLE_TYPE = register("bottle_type",
			builder -> builder.persistent(BottleType.CODEC).networkSynchronized(BottleType.STREAM_CODEC));
		SEQUENCED_ASSEMBLY = register("sequenced_assembly",
			builder -> builder.persistent(SequencedAssemblyData.CODEC).networkSynchronized(SequencedAssemblyData.STREAM_CODEC));
		SCHEMATIC_DATA = registerCompoundTag("schematic_data");
		SCHEDULE_DATA = registerCompoundTag("schedule_data");
		TRACK_ITEM = registerCompoundTag("track_item");
		TRACK_TARGETING = registerCompoundTag("track_targeting");

		FIRST_PULLEY = register("first_pulley",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
		DISPLAY_LINK_POS = register("display_link_pos",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

		AIR_TANK = register("air_tank",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
		INFERRED_FROM_RECIPE = register("inferred_from_recipe",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		COLLECTING_LIGHT = register("collecting_light",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

		// --- New components ported from NeoForge 6.0.9 ---

		// Zapper / Shaper
		PLACEMENT_PATTERN = register("placement_pattern",
			builder -> builder.persistent(PlacementPatterns.CODEC).networkSynchronized(PlacementPatterns.STREAM_CODEC));
		SHAPER_BRUSH = register("shaper_brush",
			builder -> builder.persistent(TerrainBrushes.CODEC).networkSynchronized(TerrainBrushes.STREAM_CODEC));
		SHAPER_BRUSH_PARAMS = register("shaper_brush_params",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
		SHAPER_PLACEMENT_OPTIONS = register("shaper_placement_options",
			builder -> builder.persistent(PlacementOptions.CODEC).networkSynchronized(PlacementOptions.STREAM_CODEC));
		SHAPER_TOOL = register("shaper_tool",
			builder -> builder.persistent(TerrainTools.CODEC).networkSynchronized(TerrainTools.STREAM_CODEC));
		SHAPER_BLOCK_USED = register("shaper_block_used",
			builder -> builder.persistent(BlockState.CODEC).networkSynchronized(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)));
		SHAPER_SWAP = register("shaper_swap",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		SHAPER_BLOCK_DATA = registerCompoundTag("shaper_block_data");

		// Filters
		FILTER_ITEMS = register("filter_items",
			builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));
		FILTER_ITEMS_RESPECT_NBT = register("filter_items_respect_nbt",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		FILTER_ITEMS_BLACKLIST = register("filter_items_blacklist",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		ATTRIBUTE_FILTER_WHITELIST_MODE = register("attribute_filter_whitelist_mode",
			builder -> builder.persistent(AttributeFilterWhitelistMode.CODEC).networkSynchronized(AttributeFilterWhitelistMode.STREAM_CODEC));
		ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES = register("attribute_filter_matched_attributes",
			builder -> builder.persistent(ItemAttribute.ItemAttributeEntry.CODEC.listOf())
				.networkSynchronized(ItemAttribute.ItemAttributeEntry.STREAM_CODEC.apply(ByteBufCodecs.list())));

		// Clipboard
		CLIPBOARD_CONTENT = registerCompoundTag("clipboard_content");

		// Track
		TRACK_CONNECTING_FROM = register("track_connecting_from",
			builder -> builder.persistent(ConnectingFrom.CODEC).networkSynchronized(ConnectingFrom.STREAM_CODEC));
		TRACK_EXTENDED_CURVE = register("track_extend_curve",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		TRACK_TARGETING_ITEM_SELECTED_POS = register("track_targeting_item_selected_pos",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
		TRACK_TARGETING_ITEM_SELECTED_DIRECTION = register("track_targeting_item_selected_direction",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		TRACK_TARGETING_ITEM_BEZIER = register("track_targeting_item_bezier",
			builder -> builder.persistent(BezierTrackPointLocation.CODEC).networkSynchronized(BezierTrackPointLocation.STREAM_CODEC));

		// Schematic
		SCHEMATIC_DEPLOYED = register("schematic_deployed",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		SCHEMATIC_OWNER = register("schematic_owner",
			builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
		SCHEMATIC_FILE = register("schematic_file",
			builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
		SCHEMATIC_ANCHOR = register("schematic_anchor",
			builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
		SCHEMATIC_ROTATION = register("schematic_rotation",
			builder -> builder.persistent(Rotation.CODEC).networkSynchronized(CreateStreamCodecs.ROTATION));
		SCHEMATIC_MIRROR = register("schematic_mirror",
			builder -> builder.persistent(Mirror.CODEC).networkSynchronized(CreateStreamCodecs.MIRROR));
		SCHEMATIC_BOUNDS = register("schematic_bounds",
			builder -> builder.persistent(Vec3i.CODEC).networkSynchronized(CreateStreamCodecs.VEC3I));
		SCHEMATIC_HASH = register("schematic_hash",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

		// Sand Paper
		SAND_PAPER_JEI = register("sand_paper_jei",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

		// Linked Controller
		LINKED_CONTROLLER_ITEMS = register("linked_controller_items",
			builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

		// Toolbox
		TOOLBOX_UUID = register("toolbox_uuid",
			builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));

		// Symmetry Wand
		SYMMETRY_WAND_ENABLE = register("symmetry_wand_enable",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
		SYMMETRY_WAND_SIMULATE = register("symmetry_wand_simulate",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

		// Schematicannon
		SCHEMATICANNON_OPTIONS = register("schematicannon_options",
			builder -> builder.persistent(SchematicannonOptions.CODEC).networkSynchronized(SchematicannonOptions.STREAM_CODEC));
	}

	// ---- High Logistics (Package system) ----

	public static final DataComponentType<String> PACKAGE_ADDRESS = register(
		"package_address",
		builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
	);

	public static final DataComponentType<ItemContainerContents> PACKAGE_CONTENTS = register(
		"package_contents",
		builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC)
	);

	public static final DataComponentType<PackageItem.PackageOrderData> PACKAGE_ORDER_DATA = register(
		"package_order_data",
		builder -> builder.persistent(PackageItem.PackageOrderData.CODEC).networkSynchronized(PackageItem.PackageOrderData.STREAM_CODEC)
	);

	public static final DataComponentType<PackageOrderWithCrafts> PACKAGE_ORDER_CONTEXT = register(
		"package_order_context",
		builder -> builder.persistent(PackageOrderWithCrafts.CODEC).networkSynchronized(PackageOrderWithCrafts.STREAM_CODEC)
	);

	// ============================================================
	// Helpers
	// ============================================================

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOp) {
		DataComponentType<T> type = builderOp.apply(new DataComponentType.Builder<T>())
			.cacheEncoding()
			.build();
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Create.asResource(name), type);
	}

	private static DataComponentType<CompoundTag> registerCompoundTag(String name) {
		return register(name, builder -> builder
			.persistent(CompoundTag.CODEC)
			.networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
	}
}
