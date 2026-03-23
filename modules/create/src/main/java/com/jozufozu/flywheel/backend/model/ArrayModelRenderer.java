package com.jozufozu.flywheel.backend.model;

import com.jozufozu.flywheel.core.model.Model;

/**
 * Compat stub for old Flywheel 0.6.x ArrayModelRenderer.
 */
public class ArrayModelRenderer {
	private final Model model;

	public ArrayModelRenderer(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	public void draw() {
	}

	public void delete() {
	}
}
