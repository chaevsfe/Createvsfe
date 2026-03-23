package com.jozufozu.flywheel.core.shader;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x WorldProgram.
 */
public class WorldProgram {
	protected final ResourceLocation name;
	protected final int handle;

	public WorldProgram(ResourceLocation name, int handle) {
		this.name = name;
		this.handle = handle;
	}

	public void bind() {
	}

	protected void registerSamplers() {
	}

	protected int getUniformLocation(String name) {
		return -1;
	}

	protected int setSamplerBinding(String name, int binding) {
		return -1;
	}

	protected void uploadMatrixUniform(int location, Matrix4f matrix) {
	}
}
