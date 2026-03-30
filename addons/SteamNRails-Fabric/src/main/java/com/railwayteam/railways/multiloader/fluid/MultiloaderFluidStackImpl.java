/*
 * Steam 'n' Rails
 * Copyright (c) 2024-2025 The Railways Team
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

package com.railwayteam.railways.multiloader.fluid;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiloaderFluidStackImpl extends MultiloaderFluidStack {
    final FluidStack wrapped;

    public MultiloaderFluidStackImpl(FluidStack wrapped) {
        this.wrapped = wrapped;
    }

    public MultiloaderFluidStackImpl(Fluid fluid, long amount, @Nullable CompoundTag nbt) {
        // In 1.21.1, FluidStack no longer uses CompoundTag for NBT.
        // Create a basic FluidStack; NBT data is ignored in the new DataComponents model.
        this.wrapped = new FluidStack(fluid, amount);
    }

    @Override
    public MultiloaderFluidStack setAmount(long amount) {
        wrapped.setAmount(amount);
        return this;
    }

    @Override
    public Fluid getFluid() {
        return wrapped.getFluid();
    }

    @Override
    public long getAmount() {
        return wrapped.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean isFluidEqual(MultiloaderFluidStack other) {
        if (other instanceof MultiloaderFluidStackImpl impl) {
            return wrapped.isFluidEqual(impl.wrapped);
        }
        return false;
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag nbt) {
        // In 1.21.1, FluidStack uses DataComponents, not NBT tags.
        // Write basic fluid info for backward compat.
        nbt.putString("FluidName", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(wrapped.getFluid()).toString());
        nbt.putLong("Amount", wrapped.getAmount());
        return nbt;
    }

    @Override
    public void setTag(CompoundTag tag) {
        // No-op in 1.21.1 DataComponents model
    }

    @Override
    public @Nullable CompoundTag getTag() {
        // No NBT tags in 1.21.1 DataComponents model
        return null;
    }

    @Override
    public Component getDisplayName() {
        return FluidVariantAttributes.getName(wrapped.getType());
    }

    @Override
    public FriendlyByteBuf writeToPacket(FriendlyByteBuf buffer) {
        buffer.writeUtf(net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(wrapped.getFluid()).toString());
        buffer.writeVarLong(wrapped.getAmount());
        return buffer;
    }

    @Override
    public MultiloaderFluidStack copy() {
        return new MultiloaderFluidStackImpl(new FluidStack(wrapped, wrapped.getAmount()));
    }

    @Override
    public boolean containsFluid(@NotNull MultiloaderFluidStack other) {
        if (other instanceof MultiloaderFluidStackImpl impl) {
            return isFluidEqual(other) && getAmount() >= impl.getAmount();
        }
        return false;
    }

    @Override
    public boolean isFluidStackIdentical(MultiloaderFluidStack other) {
        if (other instanceof MultiloaderFluidStackImpl impl) {
            return wrapped.isFluidEqual(impl.wrapped) && wrapped.getAmount() == impl.wrapped.getAmount();
        }
        return false;
    }

    @Override
    public boolean isFluidEqual(@NotNull ItemStack other) {
        // No easy fluid extraction from ItemStack in Fabric Transfer API without context
        return false;
    }

    @Override
    public boolean isLighterThanAir() {
        return FluidVariantAttributes.isLighterThanAir(wrapped.getType());
    }

    @Override
    public FluidIngredient asFluidIngredient() {
        return FluidIngredient.fromFluidStack(wrapped);
    }
}
