package com.simibubi.create.foundation.render;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

/**
 * GPU instance data for scrolling texture effects (simple belt segments, etc.).
 * This is the version without a full matrix transform — uses position + quaternion rotation.
 * Replaces part of old BeltData from Flywheel 0.6.x.
 */
public class ScrollInstance extends ColoredLitOverlayInstance {
	// Position
	public float x;
	public float y;
	public float z;

	// Base rotation
	public final Quaternionf rotation = new Quaternionf();

	// Scroll speed (U/V components)
	public float speedU;
	public float speedV;

	// Scroll texture diff from source to target
	public float diffU;
	public float diffV;

	// Scroll texture scale
	public float scaleU;
	public float scaleV;

	// Scroll offset
	public float offsetU;
	public float offsetV;

	public ScrollInstance(InstanceType<? extends ScrollInstance> type, InstanceHandle handle) {
		super(type, handle);
	}

	public ScrollInstance position(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollInstance position(Vec3i pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollInstance setPosition(Vec3i pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public ScrollInstance shift(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public ScrollInstance rotation(Quaternionfc q) {
		this.rotation.set(q);
		return this;
	}

	public ScrollInstance setRotation(Quaternionfc q) {
		this.rotation.set(q);
		return this;
	}

	public ScrollInstance setSpeed(float speed) {
		this.speedU = speed;
		this.speedV = speed;
		return this;
	}

	public ScrollInstance speed(float speedU, float speedV) {
		this.speedU = speedU;
		this.speedV = speedV;
		return this;
	}

	public ScrollInstance setOffset(float offset) {
		this.offsetU = offset;
		this.offsetV = offset;
		return this;
	}

	public ScrollInstance offset(float offsetU, float offsetV) {
		this.offsetU = offsetU;
		this.offsetV = offsetV;
		return this;
	}

	public ScrollInstance setScrollTexture(SpriteShiftEntry spriteShift) {
		return setSpriteShift(spriteShift);
	}

	public ScrollInstance setSpriteShift(SpriteShiftEntry spriteShift) {
		return setSpriteShift(spriteShift, 0.5f, 0.5f);
	}

	public ScrollInstance setSpriteShift(SpriteShiftEntry spriteShift, float factorU, float factorV) {
		TextureAtlasSprite target = spriteShift.getTarget();
		float spriteWidth = target.getU1() - target.getU0();
		float spriteHeight = target.getV1() - target.getV0();

		scaleU = spriteWidth * factorU;
		scaleV = spriteHeight * factorV;

		diffU = target.getU0() - spriteShift.getOriginal().getU0();
		diffV = target.getV0() - spriteShift.getOriginal().getV0();

		return this;
	}

	public ScrollInstance setScrollMult(float scrollMult) {
		// scrollMult scales the speed effect — apply to both U/V
		this.scaleU *= scrollMult;
		this.scaleV *= scrollMult;
		return this;
	}

	public ScrollInstance setColor(KineticBlockEntity be) {
		if (be.hasNetwork()) {
			setColor(Color.generateFromLong(be.network));
		} else {
			setColor(0xFF, 0xFF, 0xFF);
		}
		return this;
	}

	public ScrollInstance setColor(Color c) {
		return setColor(c.getRed(), c.getGreen(), c.getBlue());
	}

	public ScrollInstance setColor(int r, int g, int b) {
		this.red = (byte) r;
		this.green = (byte) g;
		this.blue = (byte) b;
		return this;
	}

	public ScrollInstance setBlockLight(int blockLight) {
		this.light = (this.light & 0xFFFF0000) | (blockLight & 0xFFFF);
		return this;
	}

	public ScrollInstance setSkyLight(int skyLight) {
		this.light = (this.light & 0x0000FFFF) | ((skyLight & 0xFFFF) << 16);
		return this;
	}

	public ScrollInstance setLight(int packedLight) {
		this.light = packedLight;
		return this;
	}

	public int getPackedLight() {
		return this.light;
	}
}
