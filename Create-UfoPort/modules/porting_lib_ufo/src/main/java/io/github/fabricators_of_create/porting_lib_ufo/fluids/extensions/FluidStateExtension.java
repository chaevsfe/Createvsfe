package io.github.fabricators_of_create.porting_lib_ufo.fluids.extensions;

import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidType;
import net.minecraft.world.level.material.FluidState;

public interface FluidStateExtension {
	/**
	 * Returns the type of this fluid.
	 *
	 * @return the type of this fluid
	 */
	default FluidType port_lib_ufo$getFluidType() {
		return ((FluidState) this).getType().port_lib_ufo$getFluidType();
	}
}
