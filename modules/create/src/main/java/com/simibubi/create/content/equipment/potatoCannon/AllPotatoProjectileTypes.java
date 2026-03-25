package com.simibubi.create.content.equipment.potatoCannon;

/**
 * Potato cannon projectile type initialization.
 * On Fabric, types are registered via BuiltinPotatoProjectileTypes (JSON/resource-reload system)
 * rather than the NeoForge datapack registry (BootstrapContext).
 *
 * This class initializes the codec registries for the action/render-mode sub-systems,
 * which are needed before projectile types can be loaded.
 */
public class AllPotatoProjectileTypes {

	/**
	 * Registers all action codecs and render mode codecs.
	 * Must be called before resource packs are loaded.
	 */
	public static void init() {
		AllPotatoProjectileRenderModes.init();
		AllPotatoProjectileEntityHitActions.init();
		AllPotatoProjectileBlockHitActions.init();
	}

}
