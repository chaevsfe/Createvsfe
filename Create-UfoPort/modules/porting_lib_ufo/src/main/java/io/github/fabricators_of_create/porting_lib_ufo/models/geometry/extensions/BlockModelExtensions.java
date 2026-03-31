package io.github.fabricators_of_create.porting_lib_ufo.models.geometry.extensions;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.VisibilityData;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

import java.util.function.Function;

public interface BlockModelExtensions {
	default ItemOverrides port_lib_ufo$getOverrides(ModelBaker pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib_ufo$setCustomGeometry(IUnbakedGeometry<?> geometry) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default IUnbakedGeometry<?> port_lib_ufo$getCustomGeometry() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean port_lib_ufo$isComponentVisible(String part, boolean fallback) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default VisibilityData port_lib_ufo$getVisibilityData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Transformation port_lib_ufo$getRootTransform() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib_ufo$setRootTransform(Transformation rootTransform) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
