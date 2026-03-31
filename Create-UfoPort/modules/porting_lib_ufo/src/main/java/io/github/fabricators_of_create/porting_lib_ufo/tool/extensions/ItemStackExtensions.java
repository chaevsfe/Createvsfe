package io.github.fabricators_of_create.porting_lib_ufo.tool.extensions;

import io.github.fabricators_of_create.porting_lib_ufo.tool.ToolAction;

public interface ItemStackExtensions {
	default boolean port_lib_ufo$canPerformAction(ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
