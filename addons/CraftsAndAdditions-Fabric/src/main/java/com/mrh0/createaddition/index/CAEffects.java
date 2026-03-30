package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.effect.ShockingEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CAEffects {
    public static MobEffect SHOCKING;

    public static void register() {
        SHOCKING = Registry.register(BuiltInRegistries.MOB_EFFECT, CreateAddition.asResource("shocking"),
                new ShockingEffect()
                        .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "shocking_slowdown"),
                                (double) -100f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}
