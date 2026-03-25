package com.simibubi.create.content.logistics.packagePort.postbox;

import java.lang.ref.WeakReference;
import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PostboxBlockEntity extends PackagePortBlockEntity {

	public WeakReference<GlobalStation> trackedGlobalStation = new WeakReference<>(null);
	public LerpedFloat flag;
	public boolean forceFlag;

	private boolean sendParticles;

	public PostboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		flag = LerpedFloat.linear()
			.startWithValue(0);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && !isVirtual()) {
			if (sendParticles)
				sendData();
			return;
		}

		float currentTarget = flag.getChaseTarget();
		if (currentTarget == 0 || flag.settled()) {
			int target = (inventory.isEmpty() && !forceFlag) ? 0 : 1;
			if (target != currentTarget) {
				flag.chase(target, 0.1f, Chaser.LINEAR);
				if (target == 1)
					AllSoundEvents.CONTRAPTION_ASSEMBLE.playAt(level, worldPosition, 1, 2, true);
			}
		}
		boolean settled = flag.getValue() > .15f;
		flag.tickChaser();
		if (currentTarget == 0 && settled != flag.getValue() > .15f)
			AllSoundEvents.CONTRAPTION_DISASSEMBLE.playAt(level, worldPosition, 0.75f, 1.5f, true);

		if (sendParticles) {
			sendParticles = false;
			BoneMealItem.addGrowthParticles(level, worldPosition, 40);
		}
	}

	@Override
	protected void onOpenChange(boolean open) {
		BlockState state = level.getBlockState(worldPosition);
		if (!(state.getBlock() instanceof PostboxBlock))
			return;

		level.setBlockAndUpdate(worldPosition, state.setValue(PostboxBlock.OPEN, open));
		level.playSound(null, worldPosition, open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE,
			SoundSource.BLOCKS);
	}

	public void spawnParticles() {
		sendParticles = true;
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		if (clientPacket && sendParticles)
			NBTHelper.putMarker(tag, "Particles");
		sendParticles = false;
	}

	@Override
	protected void read(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		sendParticles = clientPacket && tag.contains("Particles");
	}
}
