package com.simibubi.create;

/**
 * Stub for NeoForge's AllAttachmentTypes.
 * NeoForge uses AttachmentType (capability system) to attach MinecartController data to entities.
 * On Fabric, entity data is handled via mixins and custom NBT storage — no equivalent registry needed.
 */
public class AllAttachmentTypes {
	// No attachment types on Fabric — NeoForge capability system is not ported.
	// MinecartController data is handled via the EntityMixin custom storage approach.

	private AllAttachmentTypes() {
	}
}
