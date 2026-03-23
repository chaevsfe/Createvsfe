package com.simibubi.create.api.behaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public abstract class BlockSpoutingBehaviour {

	private static final Map<ResourceLocation, BlockSpoutingBehaviour> BLOCK_SPOUTING_BEHAVIOURS = new HashMap<>();

	public static void addCustomSpoutInteraction(ResourceLocation resourceLocation,
		BlockSpoutingBehaviour movementBehaviour) {
		BLOCK_SPOUTING_BEHAVIOURS.put(resourceLocation, movementBehaviour);
	}

	public static void forEach(Consumer<? super BlockSpoutingBehaviour> accept) {
		BLOCK_SPOUTING_BEHAVIOURS.values()
			.forEach(accept);
	}

	/**
	 * While idle, Spouts will call this every tick with simulate == true <br>
	 * When fillBlock returns &gt; 0, the Spout will start its animation cycle <br>
	 * <br>
	 * During this animation cycle, fillBlock is called once again with simulate == false but only on the relevant SpoutingBehaviour <br>
	 * When fillBlock returns &gt; 0 once again, the Spout will drain its content by the returned amount of units <br>
	 * Perform any other side-effects in this method <br>
	 * This method is called server-side only (except in ponder) <br>
	 *
	 * @param world
	 * @param pos            of the affected block
	 * @param spout
	 * @param availableFluid do not modify, return the amount to be subtracted instead
	 * @param simulate       whether the spout is testing or actually performing this behaviour
	 * @return amount filled into the block, 0 to idle/cancel
	 */
	public abstract long fillBlock(Level world, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid,
		boolean simulate);

	public static void registerDefaults() {
		addCustomSpoutInteraction(Create.asResource("dirt_to_mud"), new BlockSpoutingBehaviour() {
			@Override
			public long fillBlock(Level world, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
				BlockState state = world.getBlockState(pos);
				if (!availableFluid.getFluid().isSame(Fluids.WATER))
					return 0;
				if (!state.is(Blocks.DIRT) && !state.is(Blocks.COARSE_DIRT) && !state.is(Blocks.ROOTED_DIRT))
					return 0;
				if (availableFluid.getAmount() < 250)
					return 0;
				if (!simulate)
					world.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
				return 250;
			}
		});

		addCustomSpoutInteraction(Create.asResource("hydrate_farmland"), new BlockSpoutingBehaviour() {
			@Override
			public long fillBlock(Level world, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
				BlockState state = world.getBlockState(pos);
				if (!availableFluid.getFluid().isSame(Fluids.WATER))
					return 0;
				if (!state.is(Blocks.FARMLAND))
					return 0;
				int moisture = state.getValue(FarmBlock.MOISTURE);
				if (moisture >= 7)
					return 0;
				if (availableFluid.getAmount() < 100)
					return 0;
				if (!simulate)
					world.setBlockAndUpdate(pos, state.setValue(FarmBlock.MOISTURE, Math.min(7, moisture + 1)));
				return 100;
			}
		});

		addCustomSpoutInteraction(Create.asResource("fill_cauldron"), new BlockSpoutingBehaviour() {
			@Override
			public long fillBlock(Level world, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
				BlockState state = world.getBlockState(pos);
				// Empty cauldron + water
				if (state.is(Blocks.CAULDRON) && availableFluid.getFluid().isSame(Fluids.WATER)) {
					if (availableFluid.getAmount() < 250)
						return 0;
					if (!simulate)
						world.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState()
							.setValue(LayeredCauldronBlock.LEVEL, 1));
					return 250;
				}
				// Empty cauldron + lava
				if (state.is(Blocks.CAULDRON) && availableFluid.getFluid().isSame(Fluids.LAVA)) {
					if (availableFluid.getAmount() < 1000)
						return 0;
					if (!simulate)
						world.setBlockAndUpdate(pos, Blocks.LAVA_CAULDRON.defaultBlockState());
					return 1000;
				}
				// Water cauldron increment
				if (state.is(Blocks.WATER_CAULDRON) && availableFluid.getFluid().isSame(Fluids.WATER)) {
					int level = state.getValue(LayeredCauldronBlock.LEVEL);
					if (level >= 3)
						return 0;
					if (availableFluid.getAmount() < 250)
						return 0;
					if (!simulate)
						world.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, level + 1));
					return 250;
				}
				return 0;
			}
		});
	}

}
