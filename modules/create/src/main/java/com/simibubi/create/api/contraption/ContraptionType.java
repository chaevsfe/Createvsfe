package com.simibubi.create.api.contraption;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.Contraption;

/**
 * API facade for contraption types. Delegates to the content-layer implementation.
 * <p>
 * Registry type for contraptions, mapping type IDs to factory suppliers.
 */
public final class ContraptionType {
	public final Supplier<? extends Contraption> factory;

	public ContraptionType(Supplier<? extends Contraption> factory) {
		this.factory = factory;
	}

	/**
	 * Lookup the ContraptionType with the given ID, and create a new Contraption from it if present.
	 * If it doesn't exist, returns null.
	 */
	@Nullable
	public static Contraption fromType(String typeId) {
		return com.simibubi.create.content.contraptions.ContraptionType.fromType(typeId);
	}

	/**
	 * Register a new ContraptionType with the given ID and factory.
	 */
	public static com.simibubi.create.content.contraptions.ContraptionType register(
		String id, Supplier<? extends Contraption> factory) {
		return com.simibubi.create.content.contraptions.ContraptionType.register(id, factory);
	}
}
