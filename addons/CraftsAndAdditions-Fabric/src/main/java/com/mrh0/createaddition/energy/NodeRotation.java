package com.mrh0.createaddition.energy;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Stubbed NodeRotation enum — provides the ROTATION block state property
 * needed by connector and relay blockstates. Full rotation logic is not
 * yet ported from NeoForge.
 */
public enum NodeRotation implements StringRepresentable {
    NONE,
    Y_CLOCKWISE_90,
    Y_CLOCKWISE_180,
    Y_COUNTERCLOCKWISE_90,
    X_CLOCKWISE_90,
    X_CLOCKWISE_180,
    X_COUNTERCLOCKWISE_90,
    Z_CLOCKWISE_90,
    Z_CLOCKWISE_180,
    Z_COUNTERCLOCKWISE_90;

    public static final EnumProperty<NodeRotation> ROTATION = EnumProperty.create("rotation", NodeRotation.class);

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
