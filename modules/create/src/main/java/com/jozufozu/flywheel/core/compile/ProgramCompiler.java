package com.jozufozu.flywheel.core.compile;

import java.util.function.BiFunction;

import com.jozufozu.flywheel.core.source.FileResolution;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x ProgramCompiler.
 */
public class ProgramCompiler<P> {

	private final P program;

	@SuppressWarnings("unchecked")
	public ProgramCompiler() {
		this.program = null;
	}

	@SuppressWarnings("unchecked")
	public static <P> ProgramCompiler<P> create(Object template, BiFunction<ResourceLocation, Integer, P> factory, FileResolution header) {
		return new ProgramCompiler<>();
	}

	public P getProgram(ProgramContext context) {
		return program;
	}
}
