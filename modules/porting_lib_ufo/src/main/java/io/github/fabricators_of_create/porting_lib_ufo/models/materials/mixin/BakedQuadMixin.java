package io.github.fabricators_of_create.porting_lib_ufo.models.materials.mixin;

import net.minecraft.client.renderer.block.model.BakedQuad;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib_ufo.models.materials.MaterialData;
import io.github.fabricators_of_create.porting_lib_ufo.models.materials.extensions.BakedQuadExtensions;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadExtensions {
	private MaterialData port_lib$renderMaterial;

	@Override
	public void port_lib$setRenderMaterial(MaterialData material) {
		this.port_lib$renderMaterial = material;
	}

	@Override
	public MaterialData port_lib$getRenderMaterial() {
		return this.port_lib$renderMaterial;
	}
}
