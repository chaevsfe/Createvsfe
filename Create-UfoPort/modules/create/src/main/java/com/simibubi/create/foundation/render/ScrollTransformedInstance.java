package com.simibubi.create.foundation.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;

/**
 * GPU instance data for scrolling textures with a full matrix transform.
 * Used for belt segments that need both rotation and scroll animation.
 * Replaces old BeltData from Flywheel 0.6.x.
 */
public class ScrollTransformedInstance extends TransformedInstance {
	// Scroll speed (U/V components)
	public float speedU;
	public float speedV;

	// Scroll offset
	public float offsetU;
	public float offsetV;

	// Scroll texture diff from source to target
	public float diffU;
	public float diffV;

	// Scroll texture scale
	public float scaleU;
	public float scaleV;

	public ScrollTransformedInstance(InstanceType<? extends ScrollTransformedInstance> type, InstanceHandle handle) {
		super(type, handle);
	}

	// Backward-compatible setPosition: sets pose to identity + translation
	public ScrollTransformedInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollTransformedInstance setPosition(float x, float y, float z) {
		this.pose.identity().translate(x, y, z);
		return this;
	}

	// Backward-compatible setRotation: applies rotation to pose
	public ScrollTransformedInstance setRotation(Quaternionfc q) {
		this.pose.rotate(q);
		return this;
	}

	public ScrollTransformedInstance setSpeed(float speed) {
		this.speedU = speed;
		this.speedV = speed;
		return this;
	}

	public ScrollTransformedInstance speed(float speedU, float speedV) {
		this.speedU = speedU;
		this.speedV = speedV;
		return this;
	}

	public ScrollTransformedInstance setOffset(float offset) {
		this.offsetU = offset;
		this.offsetV = offset;
		return this;
	}

	public ScrollTransformedInstance offset(float offsetU, float offsetV) {
		this.offsetU = offsetU;
		this.offsetV = offsetV;
		return this;
	}

	public ScrollTransformedInstance setScrollTexture(SpriteShiftEntry spriteShift) {
		return setSpriteShift(spriteShift);
	}

	public ScrollTransformedInstance setSpriteShift(SpriteShiftEntry spriteShift) {
		return setSpriteShift(spriteShift, 0.5f, 0.5f);
	}

	public ScrollTransformedInstance setSpriteShift(SpriteShiftEntry spriteShift, float factorU, float factorV) {
		TextureAtlasSprite target = spriteShift.getTarget();
		float spriteWidth = target.getU1() - target.getU0();
		float spriteHeight = target.getV1() - target.getV0();

		scaleU = spriteWidth * factorU;
		scaleV = spriteHeight * factorV;

		diffU = target.getU0() - spriteShift.getOriginal().getU0();
		diffV = target.getV0() - spriteShift.getOriginal().getV0();

		return this;
	}

	public ScrollTransformedInstance setScrollMult(float scrollMult) {
		this.scaleU *= scrollMult;
		this.scaleV *= scrollMult;
		return this;
	}

	public ScrollTransformedInstance setColor(KineticBlockEntity be) {
		if (be.hasNetwork()) {
			setColor(Color.generateFromLong(be.network));
		} else {
			setColor(0xFF, 0xFF, 0xFF);
		}
		return this;
	}

	public ScrollTransformedInstance setColor(Color c) {
		return setColor(c.getRed(), c.getGreen(), c.getBlue());
	}

	public ScrollTransformedInstance setColor(int r, int g, int b) {
		this.red = (byte) r;
		this.green = (byte) g;
		this.blue = (byte) b;
		return this;
	}

	public ScrollTransformedInstance setBlockLight(int blockLight) {
		this.light = (this.light & 0xFFFF0000) | (blockLight & 0xFFFF);
		return this;
	}

	public ScrollTransformedInstance setSkyLight(int skyLight) {
		this.light = (this.light & 0x0000FFFF) | ((skyLight & 0xFFFF) << 16);
		return this;
	}

	public ScrollTransformedInstance setLight(int packedLight) {
		this.light = packedLight;
		return this;
	}

	public int getPackedLight() {
		return this.light;
	}
}
