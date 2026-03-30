package io.github.fabricators_of_create.porting_lib_ufo.models.geometry.extensions;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;

public interface TransformationExtensions {
	/**
	 * Apply this transformation to a different origin.
	 * Can be used for switching between coordinate systems.
	 * Parameter is relative to the current origin.
	 */
	default Transformation port_lib_ufo$applyOrigin(Vector3f origin) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Matrix3f port_lib_ufo$getNormalMatrix() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib_ufo$transformPosition(Vector4f position) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Direction port_lib_ufo$rotateTransform(Direction facing) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean port_lib_ufo$isIdentity() {
		return this.equals(Transformation.identity());
	}

	default void port_lib_ufo$transformNormal(Vector3f normal) {
		normal.mul(port_lib_ufo$getNormalMatrix());
		normal.normalize();
	}

	default Transformation port_lib_ufo$blockCenterToCorner() {
		return port_lib_ufo$applyOrigin(new Vector3f(.5f, .5f, .5f));
	}

	default Transformation port_lib_ufo$blockCornerToCenter() {
		return port_lib_ufo$applyOrigin(new Vector3f(-.5f, -.5f, -.5f));
	}
}
