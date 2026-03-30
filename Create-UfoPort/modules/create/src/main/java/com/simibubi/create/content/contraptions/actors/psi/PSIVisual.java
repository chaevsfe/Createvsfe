package com.simibubi.create.content.contraptions.actors.psi;

import java.util.function.Consumer;

import com.simibubi.create.foundation.utility.AngleHelper;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.minecraft.core.Direction;

public class PSIVisual extends AbstractBlockEntityVisual<PortableStorageInterfaceBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {

	private TransformedInstance middle;
	private TransformedInstance top;
	private boolean lit;
	private final float angleX;
	private final float angleY;

	public PSIVisual(VisualizationContext ctx, PortableStorageInterfaceBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		Direction facing = blockState.getValue(PortableStorageInterfaceBlock.FACING);
		angleX = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
		angleY = AngleHelper.horizontalAngle(facing);
		lit = blockEntity.isConnected();

		middle = instancerProvider().instancer(InstanceTypes.TRANSFORMED,
				Models.partial(PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit)))
			.createInstance();
		top = instancerProvider().instancer(InstanceTypes.TRANSFORMED,
				Models.partial(PortableStorageInterfaceRenderer.getTopForState(blockState)))
			.createInstance();

		animateStuff(blockEntity.getExtensionDistance(partialTick));
	}

	@Override
	public void tick(TickableVisual.Context ctx) {
		boolean nowLit = blockEntity.isConnected();
		if (lit != nowLit) {
			lit = nowLit;
			instancerProvider().instancer(InstanceTypes.TRANSFORMED,
					Models.partial(PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit)))
				.stealInstance(middle);
		}
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		animateStuff(blockEntity.getExtensionDistance(ctx.partialTick()));
	}

	private void animateStuff(float progress) {
		middle.setIdentityTransform()
			.translate(getVisualPosition())
			.center()
			.rotateYDegrees(angleY)
			.rotateXDegrees(angleX)
			.uncenter()
			.translate(0, progress * 0.5f + 0.375f, 0)
			.setChanged();

		top.setIdentityTransform()
			.translate(getVisualPosition())
			.center()
			.rotateYDegrees(angleY)
			.rotateXDegrees(angleX)
			.uncenter()
			.translate(0, progress, 0)
			.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(middle, top);
	}

	@Override
	protected void _delete() {
		middle.delete();
		top.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(middle);
		consumer.accept(top);
	}
}
