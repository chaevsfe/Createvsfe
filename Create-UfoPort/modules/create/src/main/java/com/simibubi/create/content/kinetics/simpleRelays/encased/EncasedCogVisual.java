package com.simibubi.create.content.kinetics.simpleRelays.encased;

import java.util.Optional;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for encased cogwheel (cog + optional shaft halves). Replaces old EncasedCogInstance.
 */
public class EncasedCogVisual extends KineticBlockEntityVisual<KineticBlockEntity> {

	private final boolean large;
	protected RotatingInstance rotatingModel;
	protected Optional<RotatingInstance> rotatingTopShaft;
	protected Optional<RotatingInstance> rotatingBottomShaft;

	public static EncasedCogVisual small(VisualizationContext ctx, KineticBlockEntity blockEntity, float partialTick) {
		return new EncasedCogVisual(ctx, blockEntity, false, partialTick);
	}

	public static EncasedCogVisual large(VisualizationContext ctx, KineticBlockEntity blockEntity, float partialTick) {
		return new EncasedCogVisual(ctx, blockEntity, true, partialTick);
	}

	public EncasedCogVisual(VisualizationContext ctx, KineticBlockEntity blockEntity, boolean large, float partialTick) {
		super(ctx, blockEntity, partialTick);
		this.large = large;

		rotatingModel = setup(getCogModel().createInstance());
		rotatingTopShaft = Optional.empty();
		rotatingBottomShaft = Optional.empty();

		Block block = blockState.getBlock();
		if (!(block instanceof IRotate def))
			return;

		for (Direction d : com.simibubi.create.foundation.utility.Iterate.directionsInAxis(axis)) {
			if (!def.hasShaftTowards(blockEntity.getLevel(), blockEntity.getBlockPos(), blockState, d))
				continue;
			RotatingInstance data = setup(getRotatingModel(AllPartialModels.SHAFT_HALF, blockState, d).createInstance());
			if (large)
				data.setOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos));
			if (d.getAxisDirection() == AxisDirection.POSITIVE)
				rotatingTopShaft = Optional.of(data);
			else
				rotatingBottomShaft = Optional.of(data);
		}
	}

	@Override
	public void update(float partialTick) {
		updateRotation(rotatingModel);
		rotatingTopShaft.ifPresent(this::updateRotation);
		rotatingBottomShaft.ifPresent(this::updateRotation);
	}

	@Override
	public void updateLight(float partialTick) {
		relight(rotatingModel);
		rotatingModel.handle().setChanged();
		rotatingTopShaft.ifPresent(d -> {
			relight(d);
			d.handle().setChanged();
		});
		rotatingBottomShaft.ifPresent(d -> {
			relight(d);
			d.handle().setChanged();
		});
	}

	@Override
	protected void _delete() {
		rotatingModel.handle().setDeleted();
		rotatingTopShaft.ifPresent(d -> d.handle().setDeleted());
		rotatingBottomShaft.ifPresent(d -> d.handle().setDeleted());
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(rotatingModel);
		rotatingTopShaft.ifPresent(consumer::accept);
		rotatingBottomShaft.ifPresent(consumer::accept);
	}

	protected Instancer<RotatingInstance> getCogModel() {
		BlockState referenceState = blockEntity.getBlockState();
		Direction facing =
			Direction.fromAxisAndDirection(referenceState.getValue(BlockStateProperties.AXIS), AxisDirection.POSITIVE);
		PartialModel partial = large ? AllPartialModels.SHAFTLESS_LARGE_COGWHEEL : AllPartialModels.SHAFTLESS_COGWHEEL;
		return getRotatingModel(partial, referenceState, facing);
	}
}
