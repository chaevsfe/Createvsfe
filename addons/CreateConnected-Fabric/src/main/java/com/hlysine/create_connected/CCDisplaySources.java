package com.hlysine.create_connected;

import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.source.BoilerDisplaySource;

public class CCDisplaySources {
    public static BoilerDisplaySource BOILER_STATUS;

    public static void register() {
        BOILER_STATUS = (BoilerDisplaySource) AllDisplayBehaviours.register(
                CreateConnected.asResource("boiler_status"),
                new BoilerDisplaySource()
        );
        // Assign display sources to blocks that are already registered
        AllDisplayBehaviours.assignBlock(BOILER_STATUS, CCBlocks.FLUID_VESSEL.get());
        AllDisplayBehaviours.assignBlock(BOILER_STATUS, CCBlocks.CREATIVE_FLUID_VESSEL.get());
    }
}
