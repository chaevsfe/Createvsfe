package com.simibubi.create;

import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.content.logistics.box.PackageVisual;
import com.simibubi.create.content.trains.entity.CarriageContraptionVisual;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only class that registers Flywheel visuals for entities.
 * Separated from AllEntityTypes to avoid loading Flywheel classes on dedicated servers,
 * since the JVM resolves ALL class references in a class's constant pool when the class is loaded.
 */
@Environment(EnvType.CLIENT)
public class AllEntityVisuals {

	public static void register() {
		dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer.builder(AllEntityTypes.ORIENTED_CONTRAPTION.get())
			.factory(ContraptionVisual::new).skipVanillaRender(e -> false).apply();
		dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer.builder(AllEntityTypes.CONTROLLED_CONTRAPTION.get())
			.factory(ContraptionVisual::new).skipVanillaRender(e -> false).apply();
		dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer.builder(AllEntityTypes.GANTRY_CONTRAPTION.get())
			.factory(ContraptionVisual::new).skipVanillaRender(e -> false).apply();
		dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer.builder(AllEntityTypes.CARRIAGE_CONTRAPTION.get())
			.factory(CarriageContraptionVisual::new).skipVanillaRender(e -> false).apply();
		dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer.builder(AllEntityTypes.PACKAGE.get())
			.factory(PackageVisual::new).apply();
	}
}
