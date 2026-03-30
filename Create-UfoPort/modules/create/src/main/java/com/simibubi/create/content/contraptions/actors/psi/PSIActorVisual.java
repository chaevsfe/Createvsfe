package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PSIActorVisual extends ActorVisual {

	private TransformedInstance middle;
	private TransformedInstance top;
	private boolean lit;
	private final float angleX;
	private final float angleY;

	public PSIActorVisual(VisualizationContext ctx, VirtualRenderWorld world, MovementContext movementContext) {
		super(ctx, world, movementContext);

		BlockState state = movementContext.state;
		Direction facing = state.getValue(PortableStorageInterfaceBlock.FACING);
		angleX = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
		angleY = AngleHelper.horizontalAngle(facing);
		lit = false;

		middle = instancerProvider.instancer(InstanceTypes.TRANSFORMED,
				Models.partial(PortableStorageInterfaceRenderer.getMiddleForState(state, lit)))
			.createInstance();
		top = instancerProvider.instancer(InstanceTypes.TRANSFORMED,
				Models.partial(PortableStorageInterfaceRenderer.getTopForState(state)))
			.createInstance();

		int blockLight = localBlockLight();
		middle.light(blockLight, 0);
		top.light(blockLight, 0);
	}

	@Override
	public void beginFrame() {
		LerpedFloat lf = PortableStorageInterfaceMovement.getAnimation(context);
		boolean newLit = lf.settled();
		if (lit != newLit) {
			lit = newLit;
			instancerProvider.instancer(InstanceTypes.TRANSFORMED,
					Models.partial(PortableStorageInterfaceRenderer.getMiddleForState(context.state, lit)))
				.stealInstance(middle);
		}

		float progress = lf.getValue(AnimationTickHolder.getPartialTicks());

		middle.setIdentityTransform()
			.translate(context.localPos)
			.center()
			.rotateYDegrees(angleY)
			.rotateXDegrees(angleX)
			.uncenter()
			.translate(0, progress * 0.5f + 0.375f, 0)
			.setChanged();

		top.setIdentityTransform()
			.translate(context.localPos)
			.center()
			.rotateYDegrees(angleY)
			.rotateXDegrees(angleX)
			.uncenter()
			.translate(0, progress, 0)
			.setChanged();
	}

	@Override
	protected void _delete() {
		middle.delete();
		top.delete();
	}
}
