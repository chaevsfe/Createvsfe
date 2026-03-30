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

package com.railwayteam.railways.util;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import com.google.gson.JsonObject;
import com.railwayteam.railways.multiloader.fluid.FluidUnits;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidUtils {
    public static boolean canUseAsFuelStorage(BlockEntity be)  {
        if (be instanceof FuelTankBlockEntity fuelTankBlockEntity)
            return fuelTankBlockEntity.isController();
        return false;
    }

    /**
     * @param o Either a FluidStack (forge & fabric) or FluidVariant (fabric)
     * @return The fluid
     * @throws IllegalArgumentException If any object that isn't an instance of FluidStack or FluidVariant is passed.
     */
    public static Fluid getFluid(Object o)  {
        Fluid fluid;

        if (o instanceof FluidVariant fluidVariant) {
            fluid = fluidVariant.getFluid();
        } else if (o instanceof FluidStack fluidStack) {
            fluid = fluidStack.getFluid();
        } else {
            throw new IllegalArgumentException("FluidUtils#getFluid expected to get a FluidVariant or FluidStack but got " + o.getClass().getName());
        }

        return fluid;
    }

    public static void addFluidOutput(ProcessingRecipeBuilder<ProcessingRecipe<?>> b, Fluid fluid, long amount, @Nullable CompoundTag nbt)  {
        if (nbt == null || nbt.isEmpty()) {
            b.withFluidOutputs(new FluidStack(fluid, amount));
        } else {
            net.minecraft.core.component.DataComponentPatch patch = net.minecraft.core.component.DataComponentPatch.builder()
                .set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(nbt))
                .build();
            b.withFluidOutputs(new FluidStack(net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid, patch), amount));
        }
    }

    public static void mangleFluidAmount(JsonObject json) {
        if (!json.has("amount")) return;

        long amount = json.remove("amount").getAsLong();
        json.addProperty("railways:amount", amount);
        json.addProperty("railways:amount:unit", FluidUnits.bucket());
    }

    public static void demangleFluidAmount(JsonObject json) {
        if (!json.has("railways:amount")) return;
        if (!json.has("railways:amount:unit")) return;

        long amount = json.remove("railways:amount").getAsLong();
        long unit = json.remove("railways:amount:unit").getAsLong();

        if (unit != FluidUnits.bucket()) {
            amount = (long) Math.floor(amount * (double) FluidUnits.bucket() / unit);
        }

        json.addProperty("amount", amount);
    }
}
