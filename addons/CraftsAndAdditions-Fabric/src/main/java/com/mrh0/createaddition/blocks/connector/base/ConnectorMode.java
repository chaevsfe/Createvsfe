package com.mrh0.createaddition.blocks.connector.base;

import net.minecraft.util.StringRepresentable;

public enum ConnectorMode implements StringRepresentable {
    Push("push"),
    Pull("pull"),
    None("none"),
    Passive("passive");

    private final String name;

    ConnectorMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public ConnectorMode getNext() {
        return switch (this) {
            case None -> Pull;
            case Pull -> Push;
            case Push -> None;
            default -> None;
        };
    }
}
