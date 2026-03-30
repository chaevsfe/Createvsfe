package io.github.fabricators_of_create.porting_lib_ufo.common.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public interface LanguageManagerExtensions {
	default Locale port_lib_ufo$getJavaLocale(String code) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Locale port_lib_ufo$getSelectedJavaLocale() {
		throw new RuntimeException("mixin not implemented");
	}
}
