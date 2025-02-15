package io.github.fabricators_of_create.porting_lib_ufo.core;

import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

/**
 * Core constants and utils.
 */
public class PortingLib {
	public static final String ID = "porting_lib_ufo";
	public static final String NAME = "Porting Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment()
			|| Boolean.getBoolean("portingLib.debug");

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
}
