package com.simibubi.create.content.contraptions.render;

import org.joml.Matrix4f;

import com.jozufozu.flywheel.event.BeginFrameEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import net.minecraft.util.Mth;

/**
 * Flywheel contraption renderer stub. Never instantiated since Backend.canUseInstancing() returns false.
 */
public class FlwContraption extends ContraptionRenderInfo {

	public FlwContraption(Contraption contraption, VirtualRenderWorld renderWorld) {
		super(contraption, renderWorld);
	}

	public ContraptionLighter<?> getLighter() {
		return null;
	}

	public void renderStructureLayer(Object layer, ContraptionProgram shader) {
	}

	public void renderInstanceLayer(Object event) {
	}

	@Override
	public void beginFrame(BeginFrameEvent event) {
		super.beginFrame(event);
	}

	@Override
	public void setupMatrices(PoseStack viewProjection, double camX, double camY, double camZ) {
		super.setupMatrices(viewProjection, camX, camY, camZ);
	}

	void setup(ContraptionProgram shader) {
	}

	@Override
	public void invalidate() {
	}

	public static void setupModelViewPartial(Matrix4f matrix, Matrix4f modelMatrix, AbstractContraptionEntity entity, double camX, double camY, double camZ, float pt) {
		float x = (float) (Mth.lerp(pt, entity.xOld, entity.getX()) - camX);
		float y = (float) (Mth.lerp(pt, entity.yOld, entity.getY()) - camY);
		float z = (float) (Mth.lerp(pt, entity.zOld, entity.getZ()) - camZ);
		matrix.setTranslation(x, y, z);
		matrix.mul(modelMatrix);
	}

	public void tick() {
	}
}
