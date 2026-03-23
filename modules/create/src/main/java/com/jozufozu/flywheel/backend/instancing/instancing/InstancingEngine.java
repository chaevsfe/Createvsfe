package com.jozufozu.flywheel.backend.instancing.instancing;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.Engine;
import com.jozufozu.flywheel.core.shader.WorldProgram;

/**
 * Compat stub for old Flywheel 0.6.x InstancingEngine.
 */
public class InstancingEngine<P extends WorldProgram> implements MaterialManager, Engine {
	public static <P extends WorldProgram> Builder<P> builder(Object context) {
		return new Builder<>();
	}

	public void addListener(Object listener) {
	}

	public void invalidate() {
	}

	@Override
	public void delete() {
	}

	public void beginFrame(Object info) {
	}

	public void render(Object taskEngine, Object event) {
	}

	public static class Builder<P extends WorldProgram> {
		public Builder<P> setGroupFactory(Object factory) {
			return this;
		}

		public Builder<P> setIgnoreOriginCoordinate(boolean ignore) {
			return this;
		}

		@SuppressWarnings("unchecked")
		public InstancingEngine<P> build() {
			return new InstancingEngine<>();
		}
	}

	@FunctionalInterface
	public interface GroupFactory<P extends WorldProgram> {
		Object create(Object type, Object program);
	}
}
