package com.simibubi.create.foundation.render;

import org.joml.Quaternionf;
import org.lwjgl.system.MemoryUtil;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;

/**
 * GPU instance data for scrolling textures with a quaternion transform.
 * Used for belt segments that need both rotation and scroll animation.
 * Replaces old BeltData from Flywheel 0.6.x (which always had the quaternion).
 */
public class ScrollTransformedInstance extends AbstractInstance {
	// Light
	public byte blockLight;
	public byte skyLight;

	// Color
	public byte r = (byte) 0xFF;
	public byte g = (byte) 0xFF;
	public byte b = (byte) 0xFF;
	public byte a = (byte) 0xFF;

	// Position
	public float x;
	public float y;
	public float z;

	// Kinetic
	public float speed;
	public float offset;

	// Quaternion rotation
	public float qX;
	public float qY;
	public float qZ;
	public float qW;

	// Scroll texture mapping
	public float sourceU;
	public float sourceV;
	public float minU;
	public float minV;
	public float maxU;
	public float maxV;
	public byte scrollMult;

	public ScrollTransformedInstance(InstanceType<?> type, InstanceHandle handle) {
		super(type, handle);
	}

	public ScrollTransformedInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollTransformedInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public ScrollTransformedInstance setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public ScrollTransformedInstance setOffset(float offset) {
		this.offset = offset;
		return this;
	}

	public ScrollTransformedInstance setRotation(Quaternionf q) {
		this.qX = q.x();
		this.qY = q.y();
		this.qZ = q.z();
		this.qW = q.w();
		return this;
	}

	public ScrollTransformedInstance setScrollTexture(SpriteShiftEntry spriteShift) {
		TextureAtlasSprite source = spriteShift.getOriginal();
		TextureAtlasSprite target = spriteShift.getTarget();
		this.sourceU = source.getU0();
		this.sourceV = source.getV0();
		this.minU = target.getU0();
		this.minV = target.getV0();
		this.maxU = target.getU1();
		this.maxV = target.getV1();
		return this;
	}

	public ScrollTransformedInstance setScrollMult(float scrollMult) {
		this.scrollMult = (byte) (scrollMult * 127);
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
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		return this;
	}

	public ScrollTransformedInstance setBlockLight(int blockLight) {
		this.blockLight = (byte) (blockLight & 0xF);
		return this;
	}

	public ScrollTransformedInstance setSkyLight(int skyLight) {
		this.skyLight = (byte) (skyLight & 0xF);
		return this;
	}

	public ScrollTransformedInstance setLight(int packedLight) {
		this.blockLight = (byte) (packedLight & 0xF);
		this.skyLight = (byte) ((packedLight >> 16) & 0xF);
		return this;
	}

	public int getPackedLight() {
		return (blockLight & 0xFF) | ((skyLight & 0xFF) << 16);
	}

	public static void write(long ptr, ScrollTransformedInstance i) {
		MemoryUtil.memPutShort(ptr, (short) (i.blockLight & 0xFF));
		MemoryUtil.memPutShort(ptr + 2, (short) (i.skyLight & 0xFF));
		MemoryUtil.memPutByte(ptr + 4, i.r);
		MemoryUtil.memPutByte(ptr + 5, i.g);
		MemoryUtil.memPutByte(ptr + 6, i.b);
		MemoryUtil.memPutByte(ptr + 7, i.a);
		MemoryUtil.memPutFloat(ptr + 8, i.x);
		MemoryUtil.memPutFloat(ptr + 12, i.y);
		MemoryUtil.memPutFloat(ptr + 16, i.z);
		MemoryUtil.memPutFloat(ptr + 20, i.speed);
		MemoryUtil.memPutFloat(ptr + 24, i.offset);
		MemoryUtil.memPutFloat(ptr + 28, i.qX);
		MemoryUtil.memPutFloat(ptr + 32, i.qY);
		MemoryUtil.memPutFloat(ptr + 36, i.qZ);
		MemoryUtil.memPutFloat(ptr + 40, i.qW);
		MemoryUtil.memPutFloat(ptr + 44, i.sourceU);
		MemoryUtil.memPutFloat(ptr + 48, i.sourceV);
		MemoryUtil.memPutFloat(ptr + 52, i.minU);
		MemoryUtil.memPutFloat(ptr + 56, i.minV);
		MemoryUtil.memPutFloat(ptr + 60, i.maxU);
		MemoryUtil.memPutFloat(ptr + 64, i.maxV);
		MemoryUtil.memPutByte(ptr + 68, i.scrollMult);
	}
}
