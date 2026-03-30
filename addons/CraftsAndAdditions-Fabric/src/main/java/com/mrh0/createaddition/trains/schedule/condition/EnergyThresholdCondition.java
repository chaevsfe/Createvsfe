package com.mrh0.createaddition.trains.schedule.condition;
import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
public class EnergyThresholdCondition extends ScheduleWaitCondition {
    @Override public boolean tickCompletion(Level level, Train train, CompoundTag tag) { return false; }
    @Override public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) { return Component.literal("Energy threshold"); }
    @Override public ResourceLocation getId() { return CreateAddition.asResource("energy_threshold"); }
    @Override public com.simibubi.create.foundation.utility.Pair<net.minecraft.world.item.ItemStack, Component> getSummary() { return com.simibubi.create.foundation.utility.Pair.of(net.minecraft.world.item.ItemStack.EMPTY, Component.literal("Energy threshold")); }
}
