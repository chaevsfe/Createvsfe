package io.github.fabricators_of_create.porting_lib_ufo.models.generators.extensions;

import io.github.fabricators_of_create.porting_lib_ufo.models.materials.MaterialData;

public interface BlockElementFaceExtensions {
	void port_lib$setRenderMaterial(MaterialData material);

	MaterialData port_lib$getRenderMaterial();
}
