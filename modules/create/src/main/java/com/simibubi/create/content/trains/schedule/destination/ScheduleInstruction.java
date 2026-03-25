package com.simibubi.create.content.trains.schedule.destination;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class ScheduleInstruction extends ScheduleDataEntry {

	public abstract boolean supportsConditions();

	/**
	 * Called when this instruction is the current entry in the schedule.
	 * Return a DiscoveredPath to navigate to, or null to stay put (or handle inline).
	 */
	@Nullable
	public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
		return null;
	}

	public final CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		CompoundTag dataCopy =  data.copy();
		writeAdditional(dataCopy);
		tag.putString("Id", getId().toString());
		tag.put("Data", dataCopy);
		return tag;
	}

	public static ScheduleInstruction fromTag(CompoundTag tag) {
		ResourceLocation location = ResourceLocation.parse(tag.getString("Id"));
		Supplier<? extends ScheduleInstruction> supplier = null;
		for (Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>> pair : Schedule.INSTRUCTION_TYPES)
			if (pair.getFirst()
				.equals(location))
				supplier = pair.getSecond();

		if (supplier == null) {
			Create.LOGGER.warn("Could not parse schedule instruction type: " + location);
			return new DestinationInstruction();
		}

		ScheduleInstruction scheduleDestination = supplier.get();
		// Left around for migration purposes. Data added in writeAdditional has moved into the "Data" tag
		scheduleDestination.readAdditional(tag);
		CompoundTag data = tag.getCompound("Data");
		scheduleDestination.readAdditional(data);
		scheduleDestination.data = data;
		return scheduleDestination;
	}

}
