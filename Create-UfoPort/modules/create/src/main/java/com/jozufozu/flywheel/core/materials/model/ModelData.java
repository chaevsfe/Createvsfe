package com.jozufozu.flywheel.core.materials.model;

import org.joml.Quaternionf;

import com.jozufozu.flywheel.core.materials.BasicData;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Compat stub for old Flywheel 0.6.x ModelData.
 * Represents instanced model transform data.
 * Provides a fluent API matching old Flywheel conventions.
 */
public class ModelData extends BasicData {

	public ModelData setTransform(PoseStack ms) {
		return this;
	}

	public ModelData setEmptyTransform() {
		return this;
	}

	public ModelData loadIdentity() {
		return this;
	}

	@Override
	public void delete() {
		// no-op
	}

	// --- Fluent transform methods matching old Flywheel 0.6.x Transform interface ---

	public ModelData translate(double x, double y, double z) {
		return this;
	}

	public ModelData translate(BlockPos pos) {
		return translate(pos.getX(), pos.getY(), pos.getZ());
	}

	public ModelData translate(net.minecraft.world.phys.Vec3 vec) {
		return translate(vec.x, vec.y, vec.z);
	}

	public ModelData translateBack(net.minecraft.world.phys.Vec3 vec) {
		return translate(-vec.x, -vec.y, -vec.z);
	}

	public ModelData multiply(Quaternionf quaternion) {
		return this;
	}

	public ModelData rotate(Direction axis, float radians) {
		return this;
	}

	public ModelData rotateX(float angle) {
		return this;
	}

	public ModelData rotateX(double angle) {
		return this;
	}

	public ModelData rotateY(float angle) {
		return this;
	}

	public ModelData rotateY(double angle) {
		return this;
	}

	public ModelData rotateZ(float angle) {
		return this;
	}

	public ModelData rotateZ(double angle) {
		return this;
	}

	public ModelData rotateXDegrees(float angle) {
		return this;
	}

	public ModelData rotateYDegrees(float angle) {
		return this;
	}

	public ModelData rotateZDegrees(float angle) {
		return this;
	}

	public ModelData scale(float x, float y, float z) {
		return this;
	}

	public ModelData center() {
		return translate(-0.5, -0.5, -0.5);
	}

	public ModelData uncenter() {
		return translate(0.5, 0.5, 0.5);
	}

	public ModelData rotateToFace(Direction facing) {
		return this;
	}

	public ModelData rotateCentered(Quaternionf q) {
		return this;
	}

	public ModelData nudge(int seed) {
		return this;
	}

	public ModelData translateBack(float x, float y, float z) {
		return translate(-x, -y, -z);
	}

	public ModelData translateBack(int x, float y, float z) {
		return translate(-x, -y, -z);
	}
}
