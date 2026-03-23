package com.simibubi.create.foundation.render;

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
 * GPU instance data for scrolling texture effects (simple belt segments, etc.).
 * This is the version without a quaternion transform — for belts that don't need rotation.
 * Replaces part of old BeltData from Flywheel 0.6.x.
 */
public class ScrollInstance extends AbstractInstance {
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

	// Scroll texture mapping
	public float sourceU;
	public float sourceV;
	public float minU;
	public float minV;
	public float maxU;
	public float maxV;
	public byte scrollMult;

	public ScrollInstance(InstanceType<?> type, InstanceHandle handle) {
		super(type, handle);
	}

	public ScrollInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public ScrollInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public ScrollInstance setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public ScrollInstance setOffset(float offset) {
		this.offset = offset;
		return this;
	}

	public ScrollInstance setScrollTexture(SpriteShiftEntry spriteShift) {
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

	public ScrollInstance setScrollMult(float scrollMult) {
		this.scrollMult = (byte) (scrollMult * 127);
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
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		return this;
	}

	public ScrollInstance setBlockLight(int blockLight) {
		this.blockLight = (byte) (blockLight & 0xF);
		return this;
	}

	public ScrollInstance setSkyLight(int skyLight) {
		this.skyLight = (byte) (skyLight & 0xF);
		return this;
	}

	public ScrollInstance setLight(int packedLight) {
		this.blockLight = (byte) (packedLight & 0xF);
		this.skyLight = (byte) ((packedLight >> 16) & 0xF);
		return this;
	}

	public int getPackedLight() {
		return (blockLight & 0xFF) | ((skyLight & 0xFF) << 16);
	}

	public static void write(long ptr, ScrollInstance i) {
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
		MemoryUtil.memPutFloat(ptr + 28, i.sourceU);
		MemoryUtil.memPutFloat(ptr + 32, i.sourceV);
		MemoryUtil.memPutFloat(ptr + 36, i.minU);
		MemoryUtil.memPutFloat(ptr + 40, i.minV);
		MemoryUtil.memPutFloat(ptr + 44, i.maxU);
		MemoryUtil.memPutFloat(ptr + 48, i.maxV);
		MemoryUtil.memPutByte(ptr + 52, i.scrollMult);
	}
}
