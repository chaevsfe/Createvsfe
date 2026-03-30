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

import com.railwayteam.railways.mixin_interfaces.IGetBezierConnection;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackVisual.class, remap = false)
public abstract class MixinTrackInstance extends AbstractVisual implements IGetBezierConnection {
    protected MixinTrackInstance(VisualizationContext ctx, Level level, float partialTick) {
        super(ctx, level, partialTick);
    }

    @Unique
    @Nullable
    private BezierConnection railways$bezierConnection = null;

    @Override
    public @Nullable BezierConnection getBezierConnection() {
        return railways$bezierConnection;
    }

    @Inject(method = "createInstance", at = @At("HEAD"))
    private void railways$preCreateInstance(BezierConnection bc, CallbackInfoReturnable<?> cir) {
        this.railways$bezierConnection = bc;
    }

    // TODO: Rewrite track casing rendering using new Flywheel 1.0.6 API
    // The old MaterialManager/ModelData/LightUpdater API is no longer available.
}
