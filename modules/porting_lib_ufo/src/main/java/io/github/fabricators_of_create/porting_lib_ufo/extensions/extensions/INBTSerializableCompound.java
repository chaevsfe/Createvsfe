package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.ApiStatus;

import io.github.fabricators_of_create.porting_lib_ufo.core.util.INBTSerializable;

/**
 * This class exists since we can't use generics for injection. Use
 * {@link INBTSerializable <CompoundTag>} instead.
 */
@ApiStatus.Internal
public interface INBTSerializableCompound extends INBTSerializable<CompoundTag> {
}
