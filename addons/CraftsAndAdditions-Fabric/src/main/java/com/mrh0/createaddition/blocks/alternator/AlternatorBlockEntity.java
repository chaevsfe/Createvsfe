package com.mrh0.createaddition.blocks.alternator;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.sound.CASoundScapes.AmbienceGroup;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.mrh0.createaddition.energy.fabric.EnergyLookup;
import io.github.fabricators_of_create.porting_lib_ufo.util.LazyOptional;
import com.mrh0.createaddition.energy.fabric.IEnergyStorage;

public class AlternatorBlockEntity extends KineticBlockEntity {

	protected final InternalEnergyStorage energy;
	// energy handled via BlockApiLookup

	public AlternatorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		energy = new InternalEnergyStorage(Config.ALTERNATOR_CAPACITY, 0, Config.ALTERNATOR_MAX_OUTPUT);
		// energy provided via BlockApiLookup
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		String spacing = " ";
		tooltip.add(Component.literal(spacing).append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.production").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" " + Util.format(getEnergyProductionRate((int) (isSpeedRequirementFulfilled() ? getSpeed() : 0))) + "fe/t ") // fix
				.withStyle(ChatFormatting.AQUA)).append(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
		return true;
	}

	@Override
	public float calculateStressApplied() {
		float impact = Config.MAX_STRESS/256f;
		this.lastStressApplied = impact;
		return impact;
	}

	public boolean isEnergyInput(Direction side) {
		return false;
	}

	public boolean isEnergyOutput(Direction side) {
		return true; //side != getBlockState().getValue(AlternatorBlock.FACING);
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider provider, boolean clientPacket) {
		super.read(compound, provider, clientPacket);
		energy.read(compound);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
	}

	private boolean firstTickState = true;

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide()) return;
		if(firstTickState) firstTick();
		firstTickState = false;

		if(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())
			energy.internalProduceEnergy(getEnergyProductionRate((int)getSpeed()));

		for(Direction d : Direction.values()) {
			if(!isEnergyOutput(d)) continue;
			IEnergyStorage ies = getCachedEnergy(d);
			if(ies == null) continue;
			int ext = energy.extractEnergy(ies.receiveEnergy(Config.ALTERNATOR_MAX_OUTPUT, true), false);
			ies.receiveEnergy(ext, false);
		}
	}

	@Override
	public void tickAudio() {
		super.tickAudio();

		float componentSpeed = Math.abs(getSpeed());
		if (componentSpeed == 0 || !isSpeedRequirementFulfilled())
			return;

		float pitch = Mth.clamp((componentSpeed / 256f) + .5f, .5f, 1.5f);
		if (Config.AUDIO_ENABLED) CASoundScapes.play(AmbienceGroup.DYNAMO, worldPosition, pitch);
	}

	public static int getEnergyProductionRate(int rpm) {
		rpm = Math.abs(rpm);
		return (int)((double)Config.FE_RPM * ((double)Math.abs(rpm) / 256d) * Config.ALTERNATOR_EFFICIENCY);//return (int)((double)Config.FE_TO_SU.get() * ((double)Math.abs(rpm)/256d) * EFFICIENCY);
	}

	@Override
	protected Block getStressConfigKey() {
		return CABlocks.ALTERNATOR.get();
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	public void firstTick() {
		updateCache();
	};

	public void updateCache() {
		if(level.isClientSide()) return;
		for(Direction side : Direction.values()) {
			BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
			if(te == null) {
				setCache(side, null);
				continue;
			}
			IEnergyStorage le = EnergyLookup.ENERGY.find(level, te.getBlockPos(), side.getOpposite());
			setCache(side, le);
		}
	}

	private IEnergyStorage escacheUp = null;
	private IEnergyStorage escacheDown = null;
	private IEnergyStorage escacheNorth = null;
	private IEnergyStorage escacheEast = null;
	private IEnergyStorage escacheSouth = null;
	private IEnergyStorage escacheWest = null;

	public void setCache(Direction side, IEnergyStorage storage) {
		switch (side) {
			case DOWN -> escacheDown = storage;
			case EAST -> escacheEast = storage;
			case NORTH -> escacheNorth = storage;
			case SOUTH -> escacheSouth = storage;
			case UP -> escacheUp = storage;
			case WEST -> escacheWest = storage;
		}
	}

	public IEnergyStorage getCachedEnergy(Direction side) {
		return switch (side) {
			case DOWN -> escacheDown;
			case EAST -> escacheEast;
			case NORTH -> escacheNorth;
			case SOUTH -> escacheSouth;
			case UP -> escacheUp;
			case WEST -> escacheWest;
		};
	}
}
