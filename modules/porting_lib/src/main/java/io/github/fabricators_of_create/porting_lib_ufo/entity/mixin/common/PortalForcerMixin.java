package io.github.fabricators_of_create.porting_lib_ufo.entity.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.entity.ITeleporter;
import net.minecraft.world.level.portal.PortalForcer;

@Mixin(PortalForcer.class)
public class PortalForcerMixin implements ITeleporter {
	// no need to do anything, all methods are defaulted.
}
