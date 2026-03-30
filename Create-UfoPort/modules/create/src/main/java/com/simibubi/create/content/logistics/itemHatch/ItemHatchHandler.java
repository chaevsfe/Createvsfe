package com.simibubi.create.content.logistics.itemHatch;

import com.simibubi.create.AllBlocks;

/**
 * On Fabric, the item hatch "ignore sneak" behaviour is handled directly in ItemHatchBlock.useItemOn()
 * since Fabric doesn't have a RightClickBlock event with TriState. This class is retained as a stub
 * for API compatibility.
 */
public class ItemHatchHandler {

	/**
	 * Called to register any event handlers needed for the item hatch.
	 * On Fabric, no additional registration is needed — useItemOn handles everything.
	 */
	public static void register() {
		// No-op: Fabric handles block interaction directly via useItemOn
	}

}
