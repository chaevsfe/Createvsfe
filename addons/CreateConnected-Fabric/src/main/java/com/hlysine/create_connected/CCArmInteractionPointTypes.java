package com.hlysine.create_connected;

import com.hlysine.create_connected.content.kineticbattery.KineticBatteryInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;

public class CCArmInteractionPointTypes {
    public static ArmInteractionPointType KINETIC_BATTERY;

    public static void register() {
        KINETIC_BATTERY = registerType("kinetic_battery", new KineticBatteryInteractionPoint.Type(
                CreateConnected.asResource("kinetic_battery")));
    }

    private static <T extends ArmInteractionPointType> T registerType(String key, T type) {
        ArmInteractionPointType.register(type);
        return type;
    }
}
