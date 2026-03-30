package com.jozufozu.flywheel.core.materials;

import com.jozufozu.flywheel.api.InstanceData;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

/**
 * Compat stub for old Flywheel 0.6.x BasicData.
 */
public class BasicData extends InstanceData implements FlatLit<BasicData> {

	public byte blockLight;
	public byte skyLight;

	public byte r = (byte) 0xFF;
	public byte g = (byte) 0xFF;
	public byte b = (byte) 0xFF;
	public byte a = (byte) 0xFF;

	@Override
	public BasicData setBlockLight(int blockLight) {
		this.blockLight = (byte) blockLight;
		return this;
	}

	@Override
	public BasicData setSkyLight(int skyLight) {
		this.skyLight = (byte) skyLight;
		return this;
	}

	@Override
	public int getPackedLight() {
		return LightTexture.pack(this.blockLight, this.skyLight);
	}

	public BasicData setColor(int r, int g, int b) {
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		return this;
	}

	public BasicData setColor(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public BasicData setColor(int r, int g, int b, int a) {
		return setColor((byte) r, (byte) g, (byte) b, (byte) a);
	}

	public BasicData setColor(int color) {
		return setColor(color, false);
	}

	public BasicData updateLight(BlockAndTintGetter level, BlockPos pos) {
		return this;
	}

	public BasicData setColor(int color, boolean hasAlpha) {
		byte r = (byte) ((color >> 16) & 0xFF);
		byte g = (byte) ((color >> 8) & 0xFF);
		byte b = (byte) (color & 0xFF);
		if (hasAlpha) {
			byte a = (byte) ((color >> 24) & 0xFF);
			return setColor(r, g, b, a);
		}
		return setColor(r & 0xFF, g & 0xFF, b & 0xFF);
	}
}
