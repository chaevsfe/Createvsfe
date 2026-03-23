package com.jozufozu.flywheel.core.model;

import java.util.function.BiConsumer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Compat stub for old Flywheel 0.6.x ModelTransformer.
 */
public class ModelTransformer {
	public static class Params {
		public Params light(int packedLight) {
			return this;
		}

		public Params translate(double x, double y, double z) {
			return this;
		}

		public Params multiply(Quaternionf quaternion) {
			return this;
		}

		public Params center() {
			return this;
		}

		public Params uncenter() {
			return this;
		}

		public Params color(byte r, byte g, byte b, byte a) {
			return this;
		}

		public Params rotateY(double angle) {
			return this;
		}

		public Params rotateX(double angle) {
			return this;
		}

		public Params translateBack(float x, float y, float z) {
			return this;
		}

		public Params shiftUV(ShiftUVConsumer consumer) {
			return this;
		}
	}

	@FunctionalInterface
	public interface ShiftUVConsumer {
		void accept(UVBuilder builder, float u, float v);
	}

	public static class UVBuilder {
		public void setUv(float u, float v) {
		}
	}
}
