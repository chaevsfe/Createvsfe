package com.simibubi.create.content.kinetics.base;

import java.util.function.Consumer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.RotatingInstance;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple Visual for kinetic blocks that have a single rotating model on one axis.
 * Flywheel 1.0.6 replacement for SingleRotatingInstance.
 *
 * <p>This handles the common case of blocks like shafts, drills, fans, etc.
 * that just rotate a single model around their axis.
 */
public class SingleAxisRotatingVisual<T extends KineticBlockEntity> extends KineticBlockEntityVisual<T>
	implements SimpleTickableVisual {

	protected final RotatingInstance rotatingModel;

	/**
	 * Constructor with explicit model (NeoForge-compatible).
	 */
	public SingleAxisRotatingVisual(VisualizationContext ctx, T blockEntity, float partialTick, Model model) {
		this(ctx, blockEntity, partialTick, Direction.UP, model);
	}

	/**
	 * Constructor with explicit model and source direction for rotation alignment.
	 */
	public SingleAxisRotatingVisual(VisualizationContext ctx, T blockEntity, float partialTick, Direction from, Model model) {
		super(ctx, blockEntity, partialTick);
		rotatingModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, model)
			.createInstance()
			.rotateToFace(from, rotationAxis())
			.setup(blockEntity)
			.setPosition(getVisualPosition());
		rotatingModel.setChanged();
	}

	/**
	 * Constructor using block state model with default UP orientation.
	 */
	public SingleAxisRotatingVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		this(ctx, blockEntity, partialTick, Direction.UP, blockStateModel(blockEntity.getBlockState()));
	}

	// ---- Static factory methods (NeoForge-compatible) ----

	public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(
		dev.engine_room.flywheel.lib.model.baked.PartialModel partial) {
		return (context, blockEntity, partialTick) ->
			new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.partial(partial));
	}

	/**
	 * For partial models whose source model is aligned with the Z axis instead of Y.
	 */
	public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> ofZ(
		dev.engine_room.flywheel.lib.model.baked.PartialModel partial) {
		return (context, blockEntity, partialTick) ->
			new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Direction.SOUTH, Models.partial(partial));
	}

	public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> shaft(
		VisualizationContext context, T blockEntity, float partialTick) {
		return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.partial(AllPartialModels.SHAFT_HALF));
	}

	public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> backtank(
		VisualizationContext context, T blockEntity, float partialTick) {
		var model = Models.partial(BacktankRenderer.getShaftModel(blockEntity.getBlockState()));
		return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, model);
	}

	@Override
	public void update(float partialTick) {
		rotatingModel.setup(blockEntity)
			.setChanged();
	}

	@Override
	public void tick(Context context) {
		// Subclasses can override for tick-based effects
	}

	@Override
	public void updateLight(float partialTick) {
		relight(rotatingModel);
	}

	@Override
	protected void _delete() {
		rotatingModel.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(rotatingModel);
	}

	protected BlockState getRenderedBlockState() {
		return blockState;
	}

	protected Instancer<RotatingInstance> getModel() {
		return getRotatingModel(getRenderedBlockState());
	}
}
