package com.simibubi.create;

import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.world.level.block.Blocks;

public class AllContraptionMovementSettings {
	public static void registerDefaults() {
		ContraptionMovementSetting.register(Blocks.SPAWNER, () -> AllConfigs.server().kinetics.spawnerMovement.get());
		ContraptionMovementSetting.register(Blocks.BUDDING_AMETHYST, () -> AllConfigs.server().kinetics.amethystMovement.get());
		ContraptionMovementSetting.register(Blocks.OBSIDIAN, () -> AllConfigs.server().kinetics.obsidianMovement.get());
		ContraptionMovementSetting.register(Blocks.CRYING_OBSIDIAN, () -> AllConfigs.server().kinetics.obsidianMovement.get());
		ContraptionMovementSetting.register(Blocks.RESPAWN_ANCHOR, () -> AllConfigs.server().kinetics.obsidianMovement.get());
		ContraptionMovementSetting.register(Blocks.REINFORCED_DEEPSLATE, () -> AllConfigs.server().kinetics.reinforcedDeepslateMovement.get());
	}
}
