package com.mrh0.createaddition.config;

/**
 * Simplified config for Fabric. Uses hardcoded defaults instead of ForgeConfigSpec.
 * A proper implementation would use Forge Config API Port or Cloth Config.
 */
public class Config {

    // General
    public static int FE_RPM = 480;
    public static int MAX_STRESS = 16384;
    public static boolean AUDIO_ENABLED = true;

    // Electric Motor
    public static int ELECTRIC_MOTOR_RPM_RANGE = 256;
    public static int ELECTRIC_MOTOR_MAX_INPUT = 5000;
    public static int ELECTRIC_MOTOR_MINIMUM_CONSUMPTION = 8;
    public static int ELECTRIC_MOTOR_CAPACITY = 5000;

    // Alternator
    public static int ALTERNATOR_MAX_OUTPUT = 5000;
    public static int ALTERNATOR_CAPACITY = 5000;
    public static double ALTERNATOR_EFFICIENCY = 0.75;

    // Rolling Mill
    public static int ROLLING_MILL_PROCESSING_DURATION = 120;
    public static int ROLLING_MILL_STRESS = 8;

    // Wires
    public static int SMALL_CONNECTOR_MAX_INPUT = 1000;
    public static int SMALL_CONNECTOR_MAX_OUTPUT = 1000;
    public static int SMALL_CONNECTOR_MAX_LENGTH = 16;
    public static int SMALL_LIGHT_CONNECTOR_CONSUMPTION = 1;
    public static int LARGE_CONNECTOR_MAX_INPUT = 5000;
    public static int LARGE_CONNECTOR_MAX_OUTPUT = 5000;
    public static int LARGE_CONNECTOR_MAX_LENGTH = 32;
    public static boolean CONNECTOR_IGNORE_FACE_CHECK = true;
    public static boolean CONNECTOR_ALLOW_PASSIVE_IO = true;

    // Accumulator
    public static int ACCUMULATOR_MAX_INPUT = 5000;
    public static int ACCUMULATOR_MAX_OUTPUT = 5000;
    public static int ACCUMULATOR_CAPACITY = 2_000_000;
    public static int ACCUMULATOR_MAX_HEIGHT = 5;
    public static int ACCUMULATOR_MAX_WIDTH = 3;

    // PEI
    public static int PEI_MAX_INPUT = 5000;
    public static int PEI_MAX_OUTPUT = 5000;

    // Tesla Coil
    public static int TESLA_COIL_MAX_INPUT = 10000;
    public static int TESLA_COIL_CHARGE_RATE = 5000;
    public static int TESLA_COIL_RECIPE_CHARGE_RATE = 2000;
    public static int TESLA_COIL_CAPACITY = 40_000;
    public static int TESLA_COIL_HURT_ENERGY_REQUIRED = 1000;
    public static int TESLA_COIL_HURT_RANGE = 3;
    public static int TESLA_COIL_HURT_DMG_MOB = 3;
    public static int TESLA_COIL_HURT_EFFECT_TIME_MOB = 20;
    public static int TESLA_COIL_HURT_DMG_PLAYER = 2;
    public static int TESLA_COIL_HURT_EFFECT_TIME_PLAYER = 20;
    public static int TESLA_COIL_HURT_FIRE_COOLDOWN = 20;

    // Misc
    public static int DIAMOND_GRIT_SANDPAPER_USES = 1024;
    public static double BARBED_WIRE_DAMAGE = 2.0;

    public static void init() {
        // Config values are hardcoded defaults for now.
        // A proper implementation would load from a config file.
    }
}
