package com.hlysine.create_connected.content.copycat.slab;

import com.hlysine.create_connected.content.copycat.ISimpleCopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.utility.Iterate;
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
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CopycatSlabModel extends CopycatModel {

    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    public CopycatSlabModel(BakedModel originalModel) {
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
        Direction facing = state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).isPresent() ? CopycatSlabBlock.getApparentDirection(state) : Direction.UP;

        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand);

        List<BakedQuad> quads = new ArrayList<>();
        boolean isDouble = state.getOptionalValue(CopycatSlabBlock.SLAB_TYPE).orElse(SlabType.BOTTOM) == SlabType.DOUBLE;

        // 2 pieces
        for (boolean front : Iterate.trueAndFalse) {
            assemblePiece(facing, templateQuads, quads, front, false, isDouble);
        }

        // 2 more pieces for double slabs
        if (isDouble) {
            for (boolean front : Iterate.trueAndFalse) {
                assemblePiece(facing, templateQuads, quads, front, true, isDouble);
            }
        }

        return quads;
    }

    private static void assemblePiece(Direction facing, List<BakedQuad> templateQuads, List<BakedQuad> quads, boolean front, boolean topSlab, boolean isDouble) {
        int size = templateQuads.size();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 normalScaled12 = normal.scale(12 / 16f);
        Vec3 normalScaledN8 = topSlab ? normal.scale((front ? 0 : -8) / 16f) : normal.scale((front ? 8 : 0) / 16f);
        float contract = 12;
        AABB bb = CUBE_AABB.contract(normal.x * contract / 16, normal.y * contract / 16, normal.z * contract / 16);
        if (!front)
            bb = bb.move(normalScaled12);

        for (int i = 0; i < size; i++) {
            BakedQuad quad = templateQuads.get(i);
            Direction direction = quad.getDirection();

            if (front && direction == facing)
                continue;
            if (!front && direction == facing.getOpposite())
                continue;
            if (isDouble && topSlab && direction == facing)
                continue;
            if (isDouble && !topSlab && direction == facing.getOpposite())
                continue;

            quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
                    ISimpleCopycatModel.cropAndMoveInts(quad, bb, normalScaledN8)));
        }
    }
}
