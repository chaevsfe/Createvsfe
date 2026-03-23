package com.simibubi.create.content.contraptions.render;

import com.jozufozu.flywheel.event.RenderLayerEvent;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.world.level.LevelAccessor;

/**
 * Flywheel contraption manager stub. Never runs since Backend.canUseInstancing() returns false.
 */
public class FlwContraptionManager extends ContraptionRenderingWorld<FlwContraption> {

	public FlwContraptionManager(LevelAccessor world) {
		super(world);
	}

	@Override
	public void tick() {
		super.tick();
		for (FlwContraption contraption : visible) {
			contraption.tick();
		}
	}

	@Override
	public void renderLayer(RenderLayerEvent event) {
		super.renderLayer(event);
	}

	@Override
	protected FlwContraption create(Contraption c) {
		VirtualRenderWorld renderWorld = ContraptionRenderDispatcher.setupRenderWorld(world, c);
		return new FlwContraption(c, renderWorld);
	}

	@Override
	public void removeDeadRenderers() {
		boolean removed = renderInfos.values()
				.removeIf(renderer -> {
					if (renderer.isDead()) {
						renderer.invalidate();
						return true;
					}
					return false;
				});
		if (removed) collectVisible();
	}
}
