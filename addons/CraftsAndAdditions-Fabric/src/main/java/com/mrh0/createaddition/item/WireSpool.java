package com.mrh0.createaddition.item;
import net.minecraft.world.item.Item;
public class WireSpool extends Item {
    public WireSpool(Properties props) { super(props); }
    public static boolean isRemover(Item item) { return false; }
}
