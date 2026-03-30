package com.simibubi.create;

import java.util.List;
import java.util.function.Predicate;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.api.behaviour.spouting.CauldronSpoutingBehavior;
import com.simibubi.create.api.behaviour.spouting.StateChangingBehavior;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class AllBlockSpoutingBehaviours {

	static void registerDefaults() {
		Predicate<Fluid> isWater = fluid -> fluid.isSame(Fluids.WATER);
		BlockSpoutingBehaviour toMud = StateChangingBehavior.setTo(250, isWater, Blocks.MUD);

		for (net.minecraft.world.level.block.Block dirt : List.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT)) {
			BlockSpoutingBehaviour.BY_BLOCK.register(dirt, toMud);
		}

		BlockSpoutingBehaviour.BY_BLOCK.register(Blocks.FARMLAND, StateChangingBehavior.incrementingState(100, isWater, FarmBlock.MOISTURE));
		BlockSpoutingBehaviour.BY_BLOCK.register(Blocks.WATER_CAULDRON, StateChangingBehavior.incrementingState(250, isWater, LayeredCauldronBlock.LEVEL));
		BlockSpoutingBehaviour.BY_BLOCK.register(Blocks.CAULDRON, CauldronSpoutingBehavior.INSTANCE);
	}
}
