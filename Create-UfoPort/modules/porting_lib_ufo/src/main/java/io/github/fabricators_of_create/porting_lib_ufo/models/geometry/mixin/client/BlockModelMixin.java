package io.github.fabricators_of_create.porting_lib_ufo.models.geometry.mixin.client;

import java.util.List;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.VisibilityData;
import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.class)
public class BlockModelMixin implements BlockModelExtensions {
	@Shadow
	@Final
	private List<ItemOverride> overrides;
	@Shadow
	@Nullable
	public BlockModel parent;
	@Unique
	private IUnbakedGeometry<?> customModel;
	@Unique
	@Nullable
	private Transformation rootTransform;
	@Unique
	private final VisibilityData visibilityData = new VisibilityData();

	@Inject(
			method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Z)Lnet/minecraft/client/resources/model/BakedModel;",
			at = @At("HEAD"),
			cancellable = true
	)
	public void handleCustomModels(ModelBaker modelBaker, BlockModel ownerModel, Function<Material, TextureAtlasSprite> spriteGetter,
								   ModelState modelTransform, boolean guiLight3d, CallbackInfoReturnable<BakedModel> cir) {
		IUnbakedGeometry<?> geometry = port_lib_ufo$getCustomGeometry();
		if (geometry != null) {
			ItemOverrides overrides = port_lib_ufo$getOverrides(modelBaker, ownerModel, spriteGetter);
			cir.setReturnValue(geometry.bake(
					(BlockModel) (Object) this, modelBaker, spriteGetter, modelTransform, overrides, null, guiLight3d
			));
		}
	}

	@Inject(method = "resolveParents", at = @At("HEAD"))
	private void handleCustomResolveParents(Function<ResourceLocation, UnbakedModel> function, CallbackInfo ci) {
		if (port_lib_ufo$getCustomGeometry() != null)
			port_lib_ufo$getCustomGeometry().resolveParents(function, self());
	}

	@Override
	public ItemOverrides port_lib_ufo$getOverrides(ModelBaker p_250138_, BlockModel p_251800_, Function<Material, TextureAtlasSprite> spriteGetter) {
		return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(p_250138_, p_251800_, this.overrides/*, spriteGetter*/);
	}

	@Override
	public void port_lib_ufo$setCustomGeometry(IUnbakedGeometry<?> geometry) {
		this.customModel = geometry;
	}

	@Override
	public IUnbakedGeometry<?> port_lib_ufo$getCustomGeometry() {
		return this.parent != null && customModel == null ? this.parent.port_lib_ufo$getCustomGeometry() : customModel;
	}

	@Override
	public VisibilityData port_lib_ufo$getVisibilityData() {
		return this.visibilityData;
	}

	@Override
	public boolean port_lib_ufo$isComponentVisible(String part, boolean fallback) {
		return self().parent != null && !visibilityData.hasCustomVisibility(part) ?
				self().parent.port_lib_ufo$isComponentVisible(part, fallback) :
				visibilityData.isVisible(part, fallback);
	}

	@Override
	public Transformation port_lib_ufo$getRootTransform() {
		if (rootTransform != null)
			return rootTransform;
		return self().parent != null ? self().parent.port_lib_ufo$getRootTransform() : Transformation.identity();
	}

	public void port_lib_ufo$setRootTransform(Transformation rootTransform) {
		this.rootTransform = rootTransform;
	}

	private BlockModel self() {
		return (BlockModel) (Object) this;
	}
}
