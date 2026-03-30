package io.github.fabricators_of_create.porting_lib_ufo.extensions.mixin.client;

import net.minecraft.client.renderer.block.model.ItemTransform;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.ItemTransformExtensions;

@Mixin(ItemTransform.class)
public class ItemTransformMixin implements ItemTransformExtensions {
	public Vector3f rightRotation;

	@Override
	public Vector3f getRightRotation() {
		return this.rightRotation;
	}

	@Override
	public void setRightRotation(Vector3f rightRotation) {
		this.rightRotation = rightRotation;
	}
}
