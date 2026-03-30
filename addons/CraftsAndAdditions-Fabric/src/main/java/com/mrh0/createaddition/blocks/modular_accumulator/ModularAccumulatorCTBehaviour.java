package com.mrh0.createaddition.blocks.modular_accumulator;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
public class ModularAccumulatorCTBehaviour extends ConnectedTextureBehaviour {
    @Override public com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry getShift(BlockState state, Direction dir, @Nullable net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) { return null; }
    @Override public com.simibubi.create.foundation.block.connected.CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction dir) { return null; }
}
