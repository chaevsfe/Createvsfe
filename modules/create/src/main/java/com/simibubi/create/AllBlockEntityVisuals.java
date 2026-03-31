package com.simibubi.create;

import com.simibubi.create.content.contraptions.actors.psi.PSIVisual;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.simibubi.create.content.contraptions.chassis.StickerVisual;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyVisual;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageVisual;
import com.simibubi.create.content.contraptions.pulley.HosePulleyVisual;
import com.simibubi.create.content.contraptions.pulley.RopePulleyVisual;
import com.simibubi.create.content.equipment.armor.BacktankVisual;
import com.simibubi.create.content.equipment.toolbox.ToolBoxVisual;
import com.simibubi.create.content.fluids.pipes.GlassPipeVisual;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveVisual;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.belt.BeltVisual;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;
import com.simibubi.create.content.kinetics.crank.HandCrankVisual;
import com.simibubi.create.content.kinetics.crank.ValveHandleVisual;
import com.simibubi.create.content.kinetics.deployer.DeployerVisual;
import com.simibubi.create.content.kinetics.fan.FanVisual;
import com.simibubi.create.content.kinetics.flywheel.FlywheelVisual;
import com.simibubi.create.content.kinetics.gauge.GaugeVisual;
import com.simibubi.create.content.kinetics.gearbox.GearboxVisual;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmVisual;
import com.simibubi.create.content.kinetics.mixer.MixerVisual;
import com.simibubi.create.content.kinetics.press.PressVisual;
import com.simibubi.create.content.kinetics.saw.SawVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineVisual;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelVisual;
import com.simibubi.create.content.logistics.depot.EjectorVisual;
import com.simibubi.create.content.logistics.funnel.FunnelVisual;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportVisual;
import com.simibubi.create.content.logistics.packager.PackagerVisual;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelVisual;
import com.simibubi.create.content.processing.burner.BlazeBurnerVisual;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverVisual;
import com.simibubi.create.content.redstone.diodes.BrassDiodeVisual;
import com.simibubi.create.content.schematics.cannon.SchematicannonVisual;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityVisual;
import com.simibubi.create.content.trains.display.FlapDisplayRenderer;
import com.simibubi.create.content.trains.observer.TrackObserverVisual;
import com.simibubi.create.content.trains.signal.SignalVisual;
import com.simibubi.create.content.trains.track.TrackVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only class that registers Flywheel visuals for block entities.
 * Separated from AllBlockEntityTypes to avoid loading Flywheel classes on dedicated servers,
 * since the JVM resolves ALL class references in a class's constant pool when the class is loaded.
 */
@Environment(EnvType.CLIENT)
public class AllBlockEntityVisuals {

	public static void register() {
		// Schematics
		b(AllBlockEntityTypes.SCHEMATICANNON).factory(SchematicannonVisual::new).apply();

		// Kinetics
		b(AllBlockEntityTypes.BRACKETED_KINETIC).factory(BracketedKineticBlockEntityVisual::create).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.MOTOR).factory(OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF)).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.GEARBOX).factory(GearboxVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ENCASED_SHAFT).factory(ShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ENCASED_COGWHEEL).factory(EncasedCogVisual::small).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ENCASED_LARGE_COGWHEEL).factory(EncasedCogVisual::large).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ADJUSTABLE_CHAIN_GEARSHIFT).factory(ShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.CHAIN_CONVEYOR).factory(ChainConveyorVisual::new).apply();
		b(AllBlockEntityTypes.ENCASED_FAN).factory(FanVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.CLUTCH).factory(SplitShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.GEARSHIFT).factory(SplitShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.TURNTABLE).factory(SingleAxisRotatingVisual.of(AllPartialModels.TURNTABLE)).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.HAND_CRANK).factory(HandCrankVisual::new).apply();
		b(AllBlockEntityTypes.VALVE_HANDLE).factory(ValveHandleVisual::new).apply();
		b(AllBlockEntityTypes.CUCKOO_CLOCK).factory(OrientedRotatingVisual.backHorizontal(AllPartialModels.SHAFT_HALF)).apply();
		b(AllBlockEntityTypes.GANTRY_SHAFT).factory(OrientedRotatingVisual::gantryShaft).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.GANTRY_PINION).factory(GantryCarriageVisual::new).apply();
		b(AllBlockEntityTypes.MECHANICAL_PUMP).factory(SingleAxisRotatingVisual.ofZ(AllPartialModels.MECHANICAL_PUMP_COG)).apply();

		// Fluids
		b(AllBlockEntityTypes.GLASS_FLUID_PIPE).factory(GlassPipeVisual::new).apply();
		b(AllBlockEntityTypes.FLUID_VALVE).factory(FluidValveVisual::new).apply();
		b(AllBlockEntityTypes.HOSE_PULLEY).factory(HosePulleyVisual::new).apply();

		// Belt / Transport
		b(AllBlockEntityTypes.BELT).factory(BeltVisual::new).skipVanillaRender(be -> !be.shouldRenderNormally()).apply();
		b(AllBlockEntityTypes.ANDESITE_TUNNEL).factory(BeltTunnelVisual::new).apply();
		b(AllBlockEntityTypes.BRASS_TUNNEL).factory(BeltTunnelVisual::new).apply();
		b(AllBlockEntityTypes.MECHANICAL_ARM).factory(ArmVisual::new).apply();

		// Contraptions
		b(AllBlockEntityTypes.MECHANICAL_PISTON).factory(ShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.WINDMILL_BEARING).factory(BearingVisual::new).apply();
		b(AllBlockEntityTypes.MECHANICAL_BEARING).factory(BearingVisual::new).apply();
		b(AllBlockEntityTypes.CLOCKWORK_BEARING).factory(BearingVisual::new).apply();
		b(AllBlockEntityTypes.ROPE_PULLEY).factory(RopePulleyVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ELEVATOR_PULLEY).factory(ElevatorPulleyVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.STICKER).factory(StickerVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.DRILL).factory(OrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD)).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.SAW).factory(SawVisual::new).apply();
		b(AllBlockEntityTypes.PORTABLE_STORAGE_INTERFACE).factory(PSIVisual::new).apply();
		b(AllBlockEntityTypes.PORTABLE_FLUID_INTERFACE).factory(PSIVisual::new).apply();
		b(AllBlockEntityTypes.STEAM_ENGINE).factory(SteamEngineVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.POWERED_SHAFT).factory(SingleAxisRotatingVisual.of(AllPartialModels.POWERED_SHAFT)).skipVanillaRender(be -> true).apply();

		// Processing
		b(AllBlockEntityTypes.FLYWHEEL).factory(FlywheelVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.MILLSTONE).factory(SingleAxisRotatingVisual.of(AllPartialModels.MILLSTONE_COG)).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.CRUSHING_WHEEL).factory(SingleAxisRotatingVisual.of(AllPartialModels.CRUSHING_WHEEL)).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.WATER_WHEEL).factory(WaterWheelVisual::standard).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.LARGE_WATER_WHEEL).factory(WaterWheelVisual::large).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.MECHANICAL_PRESS).factory(PressVisual::new).apply();
		b(AllBlockEntityTypes.MECHANICAL_MIXER).factory(MixerVisual::new).apply();
		b(AllBlockEntityTypes.DEPLOYER).factory(DeployerVisual::new).apply();
		b(AllBlockEntityTypes.HEATER).factory(BlazeBurnerVisual::new).apply();
		b(AllBlockEntityTypes.MECHANICAL_CRAFTER).factory(SingleAxisRotatingVisual.of(AllPartialModels.SHAFTLESS_COGWHEEL)).apply();
		b(AllBlockEntityTypes.SEQUENCED_GEARSHIFT).factory(SplitShaftVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.ROTATION_SPEED_CONTROLLER).factory(ShaftVisual::new).apply();
		b(AllBlockEntityTypes.SPEEDOMETER).factory(GaugeVisual.Speed::new).apply();
		b(AllBlockEntityTypes.STRESSOMETER).factory(GaugeVisual.Stress::new).apply();

		// Redstone
		b(AllBlockEntityTypes.ANALOG_LEVER).factory(AnalogLeverVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.PULSE_EXTENDER).factory(BrassDiodeVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.PULSE_REPEATER).factory(BrassDiodeVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.PULSE_TIMER).factory(BrassDiodeVisual::new).skipVanillaRender(be -> true).apply();

		// High Logistics
		b(AllBlockEntityTypes.PACKAGE_FROGPORT).factory(FrogportVisual::new).apply();
		b(AllBlockEntityTypes.PACKAGER).factory(PackagerVisual::new).apply();
		b(AllBlockEntityTypes.REPACKAGER).factory(PackagerVisual::new).apply();

		// Logistics
		b(AllBlockEntityTypes.WEIGHTED_EJECTOR).factory(EjectorVisual::new).apply();
		b(AllBlockEntityTypes.FUNNEL).factory(FunnelVisual::new).apply();

		// Curiosities
		b(AllBlockEntityTypes.BACKTANK).factory(BacktankVisual::new).apply();
		b(AllBlockEntityTypes.TOOLBOX).factory(ToolBoxVisual::new).skipVanillaRender(be -> true).apply();

		// Trains
		b(AllBlockEntityTypes.TRACK).factory(TrackVisual::new).apply();
		b(AllBlockEntityTypes.BOGEY).factory(BogeyBlockEntityVisual::new).skipVanillaRender(be -> true).apply();
		b(AllBlockEntityTypes.FLAP_DISPLAY).factory(SingleAxisRotatingVisual.of(AllPartialModels.SHAFTLESS_COGWHEEL)).apply();
		b(AllBlockEntityTypes.TRACK_SIGNAL).factory(SignalVisual::new).apply();
		b(AllBlockEntityTypes.TRACK_OBSERVER).factory(TrackObserverVisual::new).apply();
	}

	/** Helper to create a SimpleBlockEntityVisualizer.Builder with less verbosity */
	private static <T extends net.minecraft.world.level.block.entity.BlockEntity>
	dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.Builder<T> b(
		BlockEntityEntry<T> entry) {
		return dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer.builder(entry.get());
	}
}
