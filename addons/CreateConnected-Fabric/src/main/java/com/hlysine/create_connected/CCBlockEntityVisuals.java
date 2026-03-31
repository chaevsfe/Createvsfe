package com.hlysine.create_connected;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only class for registering Flywheel visuals.
 * In a separate class so Flywheel types are never resolved on dedicated servers.
 */
@Environment(EnvType.CLIENT)
public class CCBlockEntityVisuals {
    @SuppressWarnings("unchecked")
    public static void register() {
        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.ENCASED_CHAIN_COGWHEEL.get())
            .factory(com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual::small)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.CRANK_WHEEL.get())
            .factory(com.hlysine.create_connected.content.crankwheel.CrankWheelVisual::new)
            .apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.PARALLEL_GEARBOX.get())
            .factory(com.hlysine.create_connected.content.parallelgearbox.ParallelGearboxVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.SIX_WAY_GEARBOX.get())
            .factory(com.hlysine.create_connected.content.sixwaygearbox.SixWayGearboxVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.OVERSTRESS_CLUTCH.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.SHEAR_PIN.get())
            .factory(com.hlysine.create_connected.content.shearpin.ShearPinVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.INVERTED_CLUTCH.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.INVERTED_GEARSHIFT.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.CENTRIFUGAL_CLUTCH.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.FREEWHEEL_CLUTCH.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.KINETIC_BRIDGE.get())
            .factory((ctx, blockEntity, partialTick) -> new com.hlysine.create_connected.content.kineticbridge.KineticBridgeVisual(ctx, blockEntity, partialTick, false))
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.KINETIC_BRIDGE_DESTINATION.get())
            .factory((ctx, blockEntity, partialTick) -> new com.hlysine.create_connected.content.kineticbridge.KineticBridgeVisual(ctx, blockEntity, partialTick, true))
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.BRASS_GEARBOX.get())
            .factory(com.hlysine.create_connected.content.brassgearbox.BrassGearboxVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.BRAKE.get())
            .factory(com.simibubi.create.content.kinetics.transmission.SplitShaftVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.KINETIC_BATTERY.get())
            .factory(com.hlysine.create_connected.content.kineticbattery.KineticBatteryVisual::new)
            .skipVanillaRender(be -> true).apply();

        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CCBlockEntityTypes.LINKED_ANALOG_LEVER.get())
            .factory(com.simibubi.create.content.redstone.analogLever.AnalogLeverVisual::new)
            .apply();
    }
}
