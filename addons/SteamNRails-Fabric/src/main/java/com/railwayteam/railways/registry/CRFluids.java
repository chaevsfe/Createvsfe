/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import com.railwayteam.railways.base.reload.ClientResourceReloadCallback;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CRFluids {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final FluidEntry<VirtualFluid> PAINT = registerPaint();

    public static void register() {}

    private static FluidEntry<VirtualFluid> registerPaint() {
        return REGISTRATE.virtualFluid("paint", VirtualFluid::new)
            .lang("Paint")
            .fluidAttributes(PaintFluidVariantAttributeHandler::new)
            .register();
    }

    @Environment(EnvType.CLIENT)
    public static void initRendering() {
        PaintFluidVariantRenderHandler handler = new PaintFluidVariantRenderHandler();
        VirtualFluid paintFluid = CRFluids.PAINT.get();
        FluidVariantRendering.register(paintFluid.getFlowing(), handler);
        FluidVariantRendering.register(paintFluid.getSource(), handler);
        RailwaysClient.registerReloadCallback(handler);
    }

    static class PaintFluidVariantAttributeHandler implements FluidVariantAttributeHandler {
        @Override
        public Component getName(FluidVariant fluidVariant) {
            Optional<PalettesColor> color = PaintFluid.getColor(fluidVariant.getComponents());
            if (color.isPresent()) {
                return Components.translatable("railways.paint." + color.get().getSerializedName());
            }
            return Components.translatable("fluid.railways.paint");
        }
    }

    @Environment(EnvType.CLIENT)
    static class PaintFluidVariantRenderHandler implements FluidVariantRenderHandler, ClientResourceReloadCallback {
        private TextureAtlasSprite[] sprites;

        @Override
        public TextureAtlasSprite[] getSprites(FluidVariant fluidVariant) {
            if (sprites == null) {
                reloadSprites();
            }
            return sprites;
        }

        @Override
        public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
            Optional<PalettesColor> color = PaintFluid.getColor(fluidVariant.getComponents());
            if (color.isPresent()) {
                return 0xFF000000 | color.get().getDiffuseColor();
            }
            return 0xFFFFFFFF;
        }

        private void reloadSprites() {
            var atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            sprites = new TextureAtlasSprite[] {
                atlas.getSprite(Railways.asResource("fluid/paint_still")),
                atlas.getSprite(Railways.asResource("fluid/paint_flow"))
            };
        }

        @Override
        public void onResourceManagerReload() {
            reloadSprites();
        }
    }
}
