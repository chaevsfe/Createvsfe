package com.hlysine.create_connected;

import com.hlysine.create_connected.ponder.*;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CCPonderPlugin {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateConnected.MODID);

    public static void register() {
        HELPER.forComponents(CCBlocks.ENCASED_CHAIN_COGWHEEL)
                .addStoryBoard("chain_cogwheel", ChainCogwheelScenes::chainCogwheelAsRelay, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.CRANK_WHEEL, CCBlocks.LARGE_CRANK_WHEEL)
                .addStoryBoard("crank_wheel", CrankWheelScenes::crankWheel, AllPonderTags.KINETIC_SOURCES);
        HELPER.forComponents(CCBlocks.INVERTED_CLUTCH)
                .addStoryBoard("inverted_clutch", InvertedClutchScenes::invertedClutch, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.INVERTED_GEARSHIFT)
                .addStoryBoard("inverted_gearshift", InvertedGearshiftScenes::invertedGearshift, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.PARALLEL_GEARBOX, CCItems.VERTICAL_PARALLEL_GEARBOX)
                .addStoryBoard("parallel_gearbox", ParallelGearboxScenes::parallelGearbox, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.KINETIC_BRIDGE)
                .addStoryBoard("kinetic_bridge", KineticBridgeScene::kineticBridge, AllPonderTags.KINETIC_RELAYS);
        HELPER.forComponents(CCBlocks.KINETIC_BATTERY, CCItems.CHARGED_KINETIC_BATTERY)
                .addStoryBoard("kinetic_battery", KineticBatteryScene::kineticBattery, AllPonderTags.KINETIC_SOURCES, AllPonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("kinetic_battery_chaining", KineticBatteryScene::kineticBatteryChaining, AllPonderTags.KINETIC_SOURCES, AllPonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("kinetic_battery_automation", KineticBatteryScene::kineticBatteryAutomation, AllPonderTags.KINETIC_SOURCES, AllPonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(CCBlocks.SEQUENCED_PULSE_GENERATOR)
                .addStoryBoard("sequenced_pulse_generator", SequencedPulseGeneratorScenes::pulseGenerator, AllPonderTags.REDSTONE);
        HELPER.forComponents(CCItems.LINKED_TRANSMITTER)
                .addStoryBoard("linked_transmitter", LinkedTransmitterScenes::linkedTransmitter, AllPonderTags.REDSTONE);
        HELPER.forComponents(CCBlocks.INVENTORY_ACCESS_PORT)
                .addStoryBoard("inventory_access_port", InventoryAccessPortScenes::inventoryAccessPort, AllPonderTags.LOGISTICS);
        HELPER.forComponents(CCBlocks.INVENTORY_BRIDGE)
                .addStoryBoard("inventory_bridge", InventoryBridgeScenes::inventoryBridge, AllPonderTags.LOGISTICS)
                .addStoryBoard("inventory_bridge_filter", InventoryBridgeScenes::filtering, AllPonderTags.LOGISTICS);
    }
}
