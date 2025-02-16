package io.github.fabricators_of_create.porting_lib_ufo.entity.mixin.common;

import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.entity.extensions.ItemExtensions;

@Mixin(Item.class)
public class ItemMixin implements ItemExtensions {
}
