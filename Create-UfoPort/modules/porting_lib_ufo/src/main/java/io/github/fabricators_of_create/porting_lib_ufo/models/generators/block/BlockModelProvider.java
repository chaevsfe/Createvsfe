package io.github.fabricators_of_create.porting_lib_ufo.models.generators.block;

import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib_ufo.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib_ufo.models.generators.ModelProvider;

/**
 * Stub class to extend for block model data providers, eliminates some
 * boilerplate constructor parameters.
 */
public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {

	public BlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
		super(output, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
	}

	@NotNull
	@Override
	public String getName() {
		return "Block Models: " + modid;
	}
}
