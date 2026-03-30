package com.jozufozu.flywheel.core.layout;

/**
 * Compat stub for old Flywheel 0.6.x BufferLayout.
 */
public class BufferLayout {
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		public Builder addItems(Object... items) {
			return this;
		}

		public BufferLayout build() {
			return new BufferLayout();
		}
	}
}
