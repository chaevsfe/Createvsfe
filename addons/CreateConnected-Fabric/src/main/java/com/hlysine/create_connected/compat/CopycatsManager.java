package com.hlysine.create_connected.compat;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CCConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

public class CopycatsManager {
    // Resolved Copycats+ blocks/items, populated lazily on first use
    public static final Map<String, Block> BLOCK_MAP = new HashMap<>();
    public static final Map<String, Item> ITEM_MAP = new HashMap<>();

    public static final Map<Level, Set<BlockPos>> migrationQueue = Collections.synchronizedMap(new WeakHashMap<>());

    // Names of blocks/items in the Copycats+ mod (same path as ours, different namespace)
    private static final List<String> COPYCAT_BLOCK_PATHS = List.of(
            "copycat_block", "copycat_slab", "copycat_beam", "copycat_vertical_step",
            "copycat_stairs", "copycat_fence", "copycat_fence_gate", "copycat_wall", "copycat_board"
    );
    private static final List<String> COPYCAT_ITEM_PATHS = List.of(
            "copycat_box", "copycat_catwalk"
    );
    private static boolean mapsInitialized = false;

    private static void initMaps() {
        if (mapsInitialized) return;
        mapsInitialized = true;
        if (!Mods.COPYCATS.isLoaded()) return;
        for (String path : COPYCAT_BLOCK_PATHS) {
            ResourceLocation ccKey = Mods.COPYCATS.rl(path);
            Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ccKey);
            if (block != null && block != Blocks.AIR) {
                BLOCK_MAP.put(path, block);
            }
        }
        for (String path : COPYCAT_ITEM_PATHS) {
            ResourceLocation ccKey = Mods.COPYCATS.rl(path);
            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ccKey);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
                ITEM_MAP.put(path, item);
            }
        }
    }

    public static Block convert(Block self) {
        initMaps();
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(self);
        if (!validateNamespace(key)) return self;
        Block result = BLOCK_MAP.get(key.getPath());
        if (result != null) return result;
        return self;
    }

    public static Item convert(Item self) {
        initMaps();
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(self);
        if (!validateNamespace(key)) return self;
        Item result = ITEM_MAP.get(key.getPath());
        if (result != null) return result;
        Block blockResult = BLOCK_MAP.get(key.getPath());
        if (blockResult != null) return blockResult.asItem();
        return self;
    }

    public static ItemLike convert(ItemLike self) {
        return convert(self.asItem());
    }

    public static BlockState convert(BlockState state) {
        Block converted = convert(state.getBlock());
        if (state.getBlock() == converted) return state;
        BlockState newState = converted.defaultBlockState();
        for (Property<?> property : state.getProperties()) {
            newState = copyProperty(state, newState, property);
        }
        return newState;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return from.getOptionalValue(property).map(value -> to.trySetValue(property, value)).orElse(to);
    }

    public static Block convertIfEnabled(Block block) {
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        if (!validateNamespace(key)) return block;
        if (isFeatureEnabled(key))
            return convert(block);
        return block;
    }

    public static BlockState convertIfEnabled(BlockState state) {
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!validateNamespace(key)) return state;
        if (isFeatureEnabled(key))
            return convert(state);
        return state;
    }

    public static ItemLike convertIfEnabled(ItemLike item) {
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem());
        if (!validateNamespace(key)) return item;
        if (isFeatureEnabled(key))
            return convert(item);
        return item;
    }

    private static boolean validateNamespace(ResourceLocation key) {
        return key.getNamespace().equals(CreateConnected.MODID) || key.getNamespace().equals(Mods.COPYCATS.id());
    }

    public static boolean existsInCopycats(ResourceLocation key) {
        if (!validateNamespace(key)) return false;
        String path = key.getPath();
        if (COPYCAT_BLOCK_PATHS.contains(path)) return true;
        if (COPYCAT_ITEM_PATHS.contains(path)) return true;
        return false;
    }

    public static boolean isFeatureEnabled(ResourceLocation key) {
        if (!existsInCopycats(key))
            return false;
        if (!Mods.COPYCATS.isLoaded())
            return false;
        // Use reflection to check Copycats+ feature toggle to avoid compile-time dependency
        try {
            Class<?> ftClass = Class.forName("com.copycatsplus.copycats.config.FeatureToggle");
            java.lang.reflect.Method isEnabled = ftClass.getMethod("isEnabled", ResourceLocation.class);
            Object result = isEnabled.invoke(null, Mods.COPYCATS.rl(key.getPath()));
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    public static void enqueueMigration(Level level, BlockPos pos) {
        migrationQueue
                .computeIfAbsent(level, $ -> Collections.synchronizedSet(new LinkedHashSet<>()))
                .add(pos);
    }

    public static void onLevelTick(net.minecraft.server.MinecraftServer server) {
        if (!CCConfigs.common().migrateCopycatsOnInitialize.get()) {
            migrationQueue.clear();
            return;
        }
        for (net.minecraft.server.level.ServerLevel serverLevel : server.getAllLevels()) {
            Level level = serverLevel;
            synchronized (migrationQueue) {
                if (migrationQueue.containsKey(level)) {
                    Set<BlockPos> list = migrationQueue.get(level);
                    synchronized (list) {
                        if (list.size() > 0)
                            CreateConnected.LOGGER.debug("Copycats: Migrated " + list.size() + " copycats in " + level.dimension().location());
                        for (Iterator<BlockPos> iterator = list.iterator(); iterator.hasNext(); ) {
                            BlockPos pos = iterator.next();
                            if (!level.isLoaded(pos)) {
                                continue;
                            }
                            BlockState state = level.getBlockState(pos);
                            BlockState converted = CopycatsManager.convert(state);
                            if (!converted.is(state.getBlock())) {
                                level.setBlock(pos, converted, 2 | 16 | 32);
                            }
                            // Re-set block entity to trigger Copycats+ migration
                            BlockEntity be = level.getBlockEntity(pos);
                            if (be != null)
                                level.setBlockEntity(be);
                        }
                    }
                    migrationQueue.remove(level);
                }
            }
        }
    }
}
