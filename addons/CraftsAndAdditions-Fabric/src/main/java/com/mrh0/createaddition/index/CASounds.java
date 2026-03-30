package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class CASounds {
    public static SoundEvent ELECTRIC_MOTOR_BUZZ;
    public static SoundEvent TESLA_COIL;
    public static SoundEvent ELECTRIC_CHARGE;
    public static SoundEvent LOUD_ZAP;
    public static SoundEvent LITTLE_ZAP;

    private static SoundEvent registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
        ELECTRIC_MOTOR_BUZZ = registerSoundEvent("electric_motor_buzz");
        TESLA_COIL = registerSoundEvent("tesla_coil");
        ELECTRIC_CHARGE = registerSoundEvent("electric_charge");
        LOUD_ZAP = registerSoundEvent("loud_zap");
        LITTLE_ZAP = registerSoundEvent("little_zap");
    }
}
