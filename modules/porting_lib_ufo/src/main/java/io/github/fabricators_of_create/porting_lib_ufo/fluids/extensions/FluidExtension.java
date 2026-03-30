package io.github.fabricators_of_create.porting_lib_ufo.fluids.extensions;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidType;

public interface FluidExtension {
	default FluidType port_lib_ufo$getFluidType() {
		throw new RuntimeException();
	}
}
