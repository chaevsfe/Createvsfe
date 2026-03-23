package com.jozufozu.flywheel.backend;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.config.BackendType;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * Compat stub for old Flywheel 0.6.x Backend.
 * Always returns false for instancing checks, so BER fallback renderers are used.
 */
public class Backend {
	public static boolean isFlywheelWorld(@Nullable LevelAccessor world) {
		return false;
	}

	public static boolean canUseInstancing(@Nullable Level world) {
		return false;
	}

	public static boolean isOn() {
		return false;
	}

	public static BackendType getBackendType() {
		return com.jozufozu.flywheel.config.BackendType.OFF;
	}

	public static String getBackendDescriptor() {
		return "Off (compat shim)";
	}

	public static String getBackendString() {
		return "Off";
	}

	public static void refresh() {
		// no-op
	}

	public static void init() {
		// no-op
	}

	public static boolean isGameActive() {
		return false;
	}

	public static void reloadWorldRenderers() {
		// no-op
	}
}
