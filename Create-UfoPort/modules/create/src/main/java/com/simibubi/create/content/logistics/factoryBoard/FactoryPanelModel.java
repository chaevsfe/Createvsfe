package com.simibubi.create.content.logistics.factoryBoard;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelState;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelType;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.utility.VecHelper;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FactoryPanelModel extends ForwardingBakedModel {

	public FactoryPanelModel(BakedModel originalModel) {
		this.wrapped = originalModel;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter world, BlockState state, BlockPos pos,
		Supplier<RandomSource> randomSupplier, RenderContext context) {

		FactoryPanelModelData data = new FactoryPanelModelData();
		for (PanelSlot slot : PanelSlot.values()) {
			FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(world, new FactoryPanelPosition(pos, slot));
			if (behaviour == null)
				continue;
			data.states.put(slot, behaviour.count == 0 ? PanelState.PASSIVE : PanelState.ACTIVE);
			data.type = behaviour.panelBE().restocker ? PanelType.PACKAGER : PanelType.NETWORK;
		}
		data.ponder = world instanceof PonderWorld;

		// Emit base model quads
		super.emitBlockQuads(world, state, pos, randomSupplier, context);

		// Add transformed panel quads
		RandomSource rand = randomSupplier.get();
		for (PanelSlot panelSlot : PanelSlot.values()) {
			if (data.states.containsKey(panelSlot)) {
				emitPanelQuads(context, state, panelSlot, data.type, data.states.get(panelSlot), rand, data.ponder);
			}
		}
	}

	private void emitPanelQuads(RenderContext context, BlockState state, PanelSlot slot, PanelType type,
		PanelState panelState, RandomSource rand, boolean ponder) {
		PartialModel factoryPanel = panelState == PanelState.PASSIVE
			? type == PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL : AllPartialModels.FACTORY_PANEL_RESTOCKER
			: type == PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL_WITH_BULB
				: AllPartialModels.FACTORY_PANEL_RESTOCKER_WITH_BULB;

		BakedModel panelModel = factoryPanel.get();
		java.util.List<BakedQuad> quadsToAdd = panelModel.getQuads(state, null, rand);

		float xRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(state);
		float yRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(state);

		for (BakedQuad bakedQuad : quadsToAdd) {
			int[] vertices = bakedQuad.getVertices();
			int[] transformedVertices = Arrays.copyOf(vertices, vertices.length);

			Vec3 quadNormal = Vec3.atLowerCornerOf(bakedQuad.getDirection()
				.getNormal());
			quadNormal = VecHelper.rotate(quadNormal, 180, Axis.Y);
			quadNormal = VecHelper.rotate(quadNormal, xRot + 90, Axis.X);
			quadNormal = VecHelper.rotate(quadNormal, yRot, Axis.Y);

			for (int i = 0; i < vertices.length / BakedQuadHelper.VERTEX_STRIDE; i++) {
				Vec3 vertex = BakedQuadHelper.getXYZ(vertices, i);

				vertex = vertex.add(slot.xOffset * .5, 0, slot.yOffset * .5);
				vertex = VecHelper.rotateCentered(vertex, 180, Axis.Y);
				vertex = VecHelper.rotateCentered(vertex, xRot + 90, Axis.X);
				vertex = VecHelper.rotateCentered(vertex, yRot, Axis.Y);

				BakedQuadHelper.setXYZ(transformedVertices, i, vertex);
				// Normals are packed in the BakedQuad direction field; setNormalXYZ not needed
				// on Fabric since fromVanilla() handles normal derivation from the Direction.
			}

			Direction newNormal = Direction.fromDelta((int) Math.round(quadNormal.x), (int) Math.round(quadNormal.y),
				(int) Math.round(quadNormal.z));
			if (newNormal == null)
				newNormal = Direction.UP;

			BakedQuad transformed = new BakedQuad(transformedVertices, bakedQuad.getTintIndex(), newNormal,
				bakedQuad.getSprite(), !ponder && bakedQuad.isShade());
			context.getEmitter().fromVanilla(transformed, null, newNormal);
			context.getEmitter().emit();
		}
	}

	private static class FactoryPanelModelData {
		public PanelType type;
		public EnumMap<PanelSlot, PanelState> states = new EnumMap<>(PanelSlot.class);
		private boolean ponder;
	}

}
