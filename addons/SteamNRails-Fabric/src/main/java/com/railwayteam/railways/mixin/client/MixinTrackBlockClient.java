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

package com.railwayteam.railways.mixin.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackBlock.class)
public class MixinTrackBlockClient {

    // Shift the track overlay up for bezier curves with track casings or monorail tracks.
    // Targets the first PoseTransformStack.translate(FFF) call in the bezier branch of prepareTrackOverlay
    // (the "msr.translate(0, -4/16f, 0)" call after offset subtraction).
    @Inject(method = "prepareTrackOverlay",
        at = @At(value = "INVOKE",
            target = "Ldev/engine_room/flywheel/lib/transform/PoseTransformStack;translate(FFF)Ldev/engine_room/flywheel/lib/transform/PoseTransformStack;",
            ordinal = 0, remap = false),
        remap = false)
    private void bezierShiftTrackOverlay(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint,
                                         Direction.AxisDirection direction, PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type,
                                         CallbackInfoReturnable<PartialModel> cir,
                                         @Local PoseTransformStack msr, @Local BezierConnection bc) {
        IHasTrackCasing casingBc = (IHasTrackCasing) bc;
        if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            msr.translate(0, 14/16f, 0);
            return;
        }
        // Don't shift up if the curve is a slope and the casing is under the track, rather than in it
        if (casingBc.railways$getTrackCasing() != null) {
            if (bc.tePositions.getFirst().getY() == bc.tePositions.getSecond().getY()) {
                msr.translate(0, 1 / 16f, 0);
            } else if (!casingBc.railways$isAlternate()) {
                msr.translate(0, 4 / 16f, 0);
            }
        }
    }

    // Shift the track overlay for straight tracks with casings or monorail tracks.
    // Fires before TrackRenderer.getModelAngles() which is called for both bezier and straight tracks.
    @Inject(method = "prepareTrackOverlay",
        at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/track/TrackRenderer;getModelAngles(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            remap = true),
        remap = false)
    private void blockShiftTrackOverlay(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint,
                                        Direction.AxisDirection direction, PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type,
                                        CallbackInfoReturnable<PartialModel> cir, @Local PoseTransformStack msr) {
        if (bezierPoint == null && state.getBlock() instanceof TrackBlock trackBlock && trackBlock.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            msr.translate(0, 14/16f, 0);
            return;
        }
        if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE && state.getBlock() instanceof TrackBlock trackBlock) {
            IHasTrackCasing casingTE = (IHasTrackCasing) trackTE;
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            if (casingTE.railways$getTrackCasing() != null) {
                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                TrackType trackType = trackBlock.getMaterial().trackType;
                if (spec != null)
                    msr.translate(
                        spec.getXShift(trackType),
                        (spec.getTopSurfacePixelHeight(trackType, casingTE.railways$isAlternate()) - 2)/16f,
                        spec.getZShift(trackType)
                    );
            }
        }
    }

    @Inject(method = "prepareTrackOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"), cancellable = true)
    private void skipInvisiblePhantoms(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction, PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type, CallbackInfoReturnable<PartialModel> cir, @Local BezierConnection bc) {
        if (bc.getMaterial() == CRTrackMaterials.PHANTOM && !PhantomSpriteManager.isVisible())
            cir.setReturnValue(null);
    }
}
