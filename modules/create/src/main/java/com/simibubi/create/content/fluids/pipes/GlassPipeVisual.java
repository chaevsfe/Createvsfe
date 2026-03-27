package com.simibubi.create.content.fluids.pipes;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection.Flow;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.FluidInstance;
import com.simibubi.create.foundation.utility.Iterate;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;

public class GlassPipeVisual extends AbstractBlockEntityVisual<StraightPipeBlockEntity> implements SimpleDynamicVisual {

	private int light;

	private final SmartRecycler<TextureAtlasSprite, FluidInstance> stream;
	private final SmartRecycler<TextureAtlasSprite, TransformedInstance> surface;

	public GlassPipeVisual(VisualizationContext ctx, StraightPipeBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		stream = new SmartRecycler<>(sprite -> ctx.instancerProvider().instancer(AllInstanceTypes.FLUID, FluidMesh.stream(sprite))
			.createInstance());
		surface = new SmartRecycler<>(sprite -> ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(sprite, FluidMesh.PIPE_RADIUS))
			.createInstance());
	}

	@Override
	public void beginFrame(Context ctx) {
		stream.resetCount();
		surface.resetCount();

		FluidTransportBehaviour pipe = blockEntity.getBehaviour(FluidTransportBehaviour.TYPE);
		if (pipe == null) {
			stream.discardExtra();
			surface.discardExtra();
			return;
		}

		for (Direction side : Iterate.directions) {

			Flow flow = pipe.getFlow(side);
			if (flow == null)
				continue;
			FluidStack fluidStack = flow.fluid;
			if (fluidStack.isEmpty())
				continue;
			LerpedFloat progressLerp = flow.progress;
			if (progressLerp == null)
				continue;

			float progress = progressLerp.getValue(ctx.partialTick());
			boolean inbound = flow.inbound;
			if (progress == 1) {
				if (inbound) {
					Flow opposite = pipe.getFlow(side.getOpposite());
					if (opposite == null)
						progress -= 1e-6f;
				} else {
					FluidTransportBehaviour adjacent = BlockEntityBehaviour.get(level, pos.relative(side), FluidTransportBehaviour.TYPE);
					if (adjacent == null)
						progress -= 1e-6f;
					else {
						Flow other = adjacent.getFlow(side.getOpposite());
						if (other == null || !other.inbound && !other.complete)
							progress -= 1e-6f;
					}
				}
			}

			// Fabric fluid rendering API
			Fluid fluid = fluidStack.getFluid();
			FluidVariant fluidVariant = fluidStack.getType();
			TextureAtlasSprite[] sprites = FluidVariantRendering.getSprites(fluidVariant);
			if (sprites == null || sprites.length < 2)
				continue;
			TextureAtlasSprite flowTexture = sprites[1]; // flowing texture
			TextureAtlasSprite stillTexture = sprites[0]; // still texture

			int color = FluidVariantRendering.getColor(fluidVariant);
			int blockLightIn = (light >> 4) & 0xF;
			int luminosity = Math.max(blockLightIn, FluidVariantAttributes.getLuminance(fluidVariant));
			int effectiveLight = (this.light & 0xF00000) | luminosity << 4;

			if (inbound)
				side = side.getOpposite();

			var yStart = (inbound ? 0 : .5f);
			var progressOffset = Mth.clamp(progress * .5f, 0, 1);

			var fluidInstance = stream.get(flowTexture);

			fluidInstance.setIdentityTransform()
				.translate(getVisualPosition())
				.center()
				.rotateTo(Direction.UP, side)
				.translate(0, -Translate.CENTER + yStart, 0);

			fluidInstance.light(effectiveLight)
				.colorArgb(color);

			fluidInstance.vScale = (flowTexture.getV1() - flowTexture.getV0()) * 0.5f;
			fluidInstance.v0 = flowTexture.getV0() + yStart * fluidInstance.vScale;
			fluidInstance.progress = progressOffset;

			fluidInstance.setChanged();

			if (progress != 1) {
				surface.get(stillTexture)
					.setIdentityTransform()
					.translate(getVisualPosition())
					.center()
					.rotateTo(Direction.UP, side)
					.translate(0, -Translate.CENTER + yStart + progressOffset, 0)
					.light(effectiveLight)
					.colorArgb(color)
					.setChanged();
			}
		}

		stream.discardExtra();
		surface.discardExtra();
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

	}

	@Override
	public void updateLight(float partialTick) {
		light = computePackedLight();
	}

	@Override
	protected void _delete() {
		stream.delete();
		surface.delete();
	}

}
