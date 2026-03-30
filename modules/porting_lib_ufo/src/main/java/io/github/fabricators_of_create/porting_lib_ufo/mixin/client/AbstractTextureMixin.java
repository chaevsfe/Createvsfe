package io.github.fabricators_of_create.porting_lib_ufo.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.AbstractTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions.AbstractTextureExtensions;

@Environment(EnvType.CLIENT)
@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin implements AbstractTextureExtensions {
	@Shadow
	protected boolean blur;
	@Shadow
	protected boolean mipmap;

	@Unique
	private boolean port_lib$lastBlur;
	@Unique
	private boolean port_lib$lastMipmap;

	@Shadow
	public abstract void setFilter(boolean blur, boolean mipmap);

	@Unique
	@Override
	public void port_lib_ufo$setBlurMipmap(boolean blur, boolean mipmap) {
		this.port_lib$lastBlur = this.blur;
		this.port_lib$lastMipmap = this.mipmap;
		setFilter(blur, mipmap);
	}

	@Unique
	@Override
	public void port_lib_ufo$restoreLastBlurMipmap() {
		setFilter(this.port_lib$lastBlur, this.port_lib$lastMipmap);
	}
}
