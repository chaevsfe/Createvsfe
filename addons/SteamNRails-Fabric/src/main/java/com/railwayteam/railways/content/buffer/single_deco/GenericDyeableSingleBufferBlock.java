/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.buffer.single_deco;

import com.simibubi.create.foundation.utility.VoxelShaper;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.ParametersAreNonnullByDefault;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GenericDyeableSingleBufferBlock extends AbstractDyeableSingleBufferBlock {
    protected final VoxelShaper shaper;

    // Multi-arg constructor; codec is not used for deserialization in practice
    public static final MapCodec<GenericDyeableSingleBufferBlock> CODEC = simpleCodec(
        p -> new GenericDyeableSingleBufferBlock(p, com.simibubi.create.foundation.utility.VoxelShaper.forDirectional(
            net.minecraft.world.phys.shapes.Shapes.block(), net.minecraft.core.Direction.NORTH)));

    public GenericDyeableSingleBufferBlock(Properties properties, VoxelShaper shaper) {
        super(properties);
        this.shaper = shaper;
    }

    public static NonNullFunction<Properties, GenericDyeableSingleBufferBlock> createFactory(VoxelShaper shaper) {
        return properties -> new GenericDyeableSingleBufferBlock(properties, shaper);
    }

    @Override
    protected BlockState cycleStyle(BlockState originalState, Direction targetedFace) {
        return originalState;
    }

    @Override
    protected VoxelShaper getShaper(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shaper;
    }
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

}
