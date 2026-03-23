package com.simibubi.create.foundation.render;

import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.api.layout.FloatRepr;
import dev.engine_room.flywheel.api.layout.IntegerRepr;
import dev.engine_room.flywheel.api.layout.LayoutBuilder;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Declares the Flywheel 1.0.6 InstanceType constants for Create's GPU-accelerated rendering.
 * These replace the old AllMaterialSpecs (StructType-based) from Flywheel 0.6.x.
 *
 * <p>Each InstanceType defines:
 * <ul>
 *   <li>A factory to create Instance objects</li>
 *   <li>A Layout describing the GPU buffer memory format</li>
 *   <li>A writer function to serialize instance data to native memory</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public class AllInstanceTypes {

	/**
	 * Instance type for rotating kinetic blocks (gears, shafts, drills, fans, etc.).
	 * Layout: light(2s) + color(4b) + pos(3f) + speed(f) + offset(f) + axis(3b)
	 */
	public static final InstanceType<RotatingInstance> ROTATING = SimpleInstanceType.<RotatingInstance>builder(RotatingInstance::new)
		.layout(LayoutBuilder.create()
			.vector("light", IntegerRepr.SHORT, 2)
			.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
			.vector("pos", FloatRepr.FLOAT, 3)
			.scalar("speed", FloatRepr.FLOAT)
			.scalar("offset", FloatRepr.FLOAT)
			.vector("axis", FloatRepr.NORMALIZED_BYTE, 3)
			.build())
		.writer(RotatingInstance::write)
		.build();

	/**
	 * Instance type for simple scrolling textures (conveyor-like without rotation).
	 * Layout: light(2s) + color(4b) + pos(3f) + speed(f) + offset(f)
	 *       + sourceUV(2f) + minUV(2f) + maxUV(2f) + scrollMult(1b)
	 */
	public static final InstanceType<ScrollInstance> SCROLLING = SimpleInstanceType.<ScrollInstance>builder(ScrollInstance::new)
		.layout(LayoutBuilder.create()
			.vector("light", IntegerRepr.SHORT, 2)
			.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
			.vector("pos", FloatRepr.FLOAT, 3)
			.scalar("speed", FloatRepr.FLOAT)
			.scalar("offset", FloatRepr.FLOAT)
			.vector("sourceUV", FloatRepr.FLOAT, 2)
			.vector("minUV", FloatRepr.FLOAT, 2)
			.vector("maxUV", FloatRepr.FLOAT, 2)
			.scalar("scrollMult", FloatRepr.NORMALIZED_BYTE)
			.build())
		.writer(ScrollInstance::write)
		.build();

	/**
	 * Instance type for scrolling textures with a quaternion transform.
	 * Used for belt segments that need both rotation and scroll animation.
	 * Layout: light(2s) + color(4b) + pos(3f) + speed(f) + offset(f)
	 *       + quaternion(4f) + sourceUV(2f) + minUV(2f) + maxUV(2f) + scrollMult(1b)
	 */
	public static final InstanceType<ScrollTransformedInstance> SCROLLING_TRANSFORMED = SimpleInstanceType.<ScrollTransformedInstance>builder(ScrollTransformedInstance::new)
		.layout(LayoutBuilder.create()
			.vector("light", IntegerRepr.SHORT, 2)
			.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
			.vector("pos", FloatRepr.FLOAT, 3)
			.scalar("speed", FloatRepr.FLOAT)
			.scalar("offset", FloatRepr.FLOAT)
			.vector("rotation", FloatRepr.FLOAT, 4)
			.vector("sourceUV", FloatRepr.FLOAT, 2)
			.vector("minUV", FloatRepr.FLOAT, 2)
			.vector("maxUV", FloatRepr.FLOAT, 2)
			.scalar("scrollMult", FloatRepr.NORMALIZED_BYTE)
			.build())
		.writer(ScrollTransformedInstance::write)
		.build();

	/**
	 * Instance type for fluid rendering in contraptions and tanks.
	 * Layout: light(2s) + color(4b) + pos(3f) + overlay(1i)
	 */
	public static final InstanceType<FluidInstance> FLUID = SimpleInstanceType.<FluidInstance>builder(FluidInstance::new)
		.layout(LayoutBuilder.create()
			.vector("light", IntegerRepr.SHORT, 2)
			.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
			.vector("pos", FloatRepr.FLOAT, 3)
			.scalar("overlay", IntegerRepr.INT)
			.build())
		.writer(FluidInstance::write)
		.build();

	/** Force class loading to register all instance types. */
	public static void init() {
	}
}
