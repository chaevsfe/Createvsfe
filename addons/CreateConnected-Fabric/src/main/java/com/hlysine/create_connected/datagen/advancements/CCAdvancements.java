package com.hlysine.create_connected.datagen.advancements;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.hlysine.create_connected.datagen.advancements.CCAdvancement.TaskType.*;

@SuppressWarnings("unused")
public class CCAdvancements {

    public static final List<CCAdvancement> ENTRIES = new ArrayList<>();

    public static final CCAdvancement START = null,

    ROOT = create("root", b -> b.icon(CCBlocks.PARALLEL_GEARBOX)
            .title("Welcome to Create: Connected")
            .description("Gadgets for all situations")
            .awardedForFree()
            .special(SILENT)),

    SHEAR_PIN = create("shear_pin", b -> b.icon(CCBlocks.SHEAR_PIN)
            .title("Snap!")
            .description("Blow a Shear Pin")
            .after(ROOT)),

    OVERSTRESS_CLUTCH = create("overstress_clutch", b -> b.icon(CCBlocks.OVERSTRESS_CLUTCH)
            .title("Circuit Breaker")
            .description("Trigger an Overstress Clutch")
            .after(SHEAR_PIN)),

    BRASS_GEARBOX = create("brass_gearbox", b -> b.icon(CCBlocks.BRASS_GEARBOX)
            .title("Serious Organization")
            .description("Place down a Brass Gearbox")
            .whenBlockPlaced(CCBlocks.BRASS_GEARBOX.get())
            .after(ROOT)),

    OVERPOWERED_BRAKE = create("overpowered_brake_0", b -> b.icon(CCBlocks.BRAKE)
            .title("Overpowered")
            .description("Keep a network running at speed with a powered brake attached")
            .after(ROOT)
            .special(SECRET)),

    CONTROL_CHIP = create("control_chip", b -> b.icon(CCItems.CONTROL_CHIP)
            .title("Precise Fabrication")
            .description("Assemble a Control Chip")
            .whenIconCollected()
            .after(ROOT)
            .special(NOISY)),

    SEQUENCED_PULSE_GENERATOR = create("sequenced_pulse_generator", b -> b.icon(CCBlocks.SEQUENCED_PULSE_GENERATOR)
            .title("Computational Supremacy")
            .description("Place down a Sequenced Pulse Generator")
            .whenBlockPlaced(CCBlocks.SEQUENCED_PULSE_GENERATOR.get())
            .after(CONTROL_CHIP)),

    PULSE_GEN_INFINITE_LOOP = create("pulse_generator_infinite_loop", b -> b.icon(CCItems.INCOMPLETE_CONTROL_CHIP)
            .title("Infinite Loop")
            .description("Overload a Sequenced Pulse Generator with a buggy program")
            .after(SEQUENCED_PULSE_GENERATOR)
            .special(SECRET)),

    KINETIC_BATTERY = create("kinetic_battery", b -> b.icon(CCBlocks.KINETIC_BATTERY)
            .title("Energy Independence")
            .description("Charge a Kinetic Battery")
            .after(ROOT)),

    END = null;

    private static CCAdvancement create(String id, UnaryOperator<CCAdvancement.Builder> b) {
        return new CCAdvancement(id, b);
    }

    public static void register() {
    }
}
