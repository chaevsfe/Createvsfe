package io.github.fabricators_of_create.porting_lib_ufo.mixin.client;

import net.minecraft.client.particle.Particle;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.ParticleExtensions;

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleExtensions {
}
