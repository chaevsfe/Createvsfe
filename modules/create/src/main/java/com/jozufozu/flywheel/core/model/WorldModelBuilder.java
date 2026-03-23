package com.jozufozu.flywheel.core.model;

import java.util.Collection;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Compat stub for old Flywheel 0.6.x WorldModelBuilder.
 */
public class WorldModelBuilder {
	public WorldModelBuilder(RenderType layer) {
	}

	public WorldModelBuilder withRenderWorld(LevelAccessor level) {
		return this;
	}

	public WorldModelBuilder withBlocks(Collection<StructureTemplate.StructureBlockInfo> blocks) {
		return this;
	}

	public Model toModel(String name) {
		return new Model() {};
	}

	public ShadeSeparatedBufferedData build() {
		return new ShadeSeparatedBufferedData(java.nio.ByteBuffer.allocate(0), null, 0);
	}
}
