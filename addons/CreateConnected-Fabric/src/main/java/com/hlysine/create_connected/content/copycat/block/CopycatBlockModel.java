package com.hlysine.create_connected.content.copycat.block;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CopycatBlockModel extends CopycatModel {

    public CopycatBlockModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
            Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material,
            CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData) {
        QuadEmitter emitter = context.getEmitter();
        RandomSource rand = randomSupplier.get();
        for (int i = -1; i < 6; i++) {
            Direction side = i < 0 ? null : Direction.from3DDataValue(i);
            if (side != null && occlusionData.isOccluded(side)) continue;
            List<BakedQuad> quads = getQuadsForSide(state, side, rand, material);
            for (BakedQuad quad : quads) {
                Direction cullFace = cullFaceRemovalData.shouldRemove(quad.getDirection()) ? null : quad.getDirection();
                emitter.fromVanilla(quad, RendererAccess.INSTANCE.getRenderer().materialFinder().find(), cullFace);
                emitter.emit();
            }
        }
    }

    private List<BakedQuad> getQuadsForSide(BlockState state, Direction side, RandomSource rand, BlockState material) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand);

        List<BakedQuad> quads = new ArrayList<>();

        for (BakedQuad quad : templateQuads) {
            quads.add(BakedQuadHelper.clone(quad));
        }

        return quads;
    }
}
