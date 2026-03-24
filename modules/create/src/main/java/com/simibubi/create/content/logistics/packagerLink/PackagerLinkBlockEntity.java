package com.simibubi.create.content.logistics.packagerLink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;

public class PackagerLinkBlockEntity extends LinkWithBulbBlockEntity {

	public LogisticallyLinkedBehaviour behaviour;
	public UUID placedBy;

	public PackagerLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		setLazyTickRate(10);
		placedBy = null;
	}

	public InventorySummary fetchSummaryFromPackager(@Nullable IdentifiedInventory ignoredHandler) {
		// TODO: full implementation when PackagerBlockEntity is ported
		return InventorySummary.EMPTY;
	}

	public void playEffect() {
		AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 0.75f, 1.25f, false);
		Vec3 vec3 = Vec3.atCenterOf(worldPosition);

		BlockState state = getBlockState();
		float f = 1;

		AttachFace face = state.getOptionalValue(PackagerLinkBlock.FACE)
			.orElse(AttachFace.FLOOR);
		if (face != AttachFace.FLOOR)
			f = -1;
		if (face == AttachFace.WALL)
			vec3 = vec3.add(0, 0.25, 0);

		vec3 = vec3.add(Vec3.atLowerCornerOf(state.getOptionalValue(PackagerLinkBlock.FACING)
				.orElse(Direction.SOUTH)
				.getNormal())
			.scale(f * 0.125));

		pulse();
		// WiFiParticle effect deferred until particle system is ported
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		if (placedBy != null)
			tag.putUUID("PlacedBy", placedBy);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		placedBy = tag.contains("PlacedBy") ? tag.getUUID("PlacedBy") : null;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(behaviour = new LogisticallyLinkedBehaviour(this, true));
	}

	@Override
	public void initialize() {
		super.initialize();
		behaviour.redstonePowerChanged(PackagerLinkBlock.getPower(getBlockState(), level, worldPosition));
	}

	@Override
	public Direction getBulbFacing(BlockState state) {
		return PackagerLinkBlock.getConnectedDirection(state);
	}

	private static final Map<BlockState, Vec3> bulbOffsets = new HashMap<>();

	@Override
	public Vec3 getBulbOffset(BlockState state) {
		return bulbOffsets.computeIfAbsent(state, s -> {
			Vec3 offset = VecHelper.voxelSpace(5, 6, 11);
			Vec3 wallOffset = VecHelper.voxelSpace(11, 6, 5);
			AttachFace face = s.getValue(PackagerLinkBlock.FACE);
			Vec3 vec = face == AttachFace.WALL ? wallOffset : offset;
			float angle = AngleHelper.horizontalAngle(s.getValue(PackagerLinkBlock.FACING));
			if (face == AttachFace.CEILING)
				angle = -angle;
			if (face == AttachFace.WALL)
				angle = 0;
			return VecHelper.rotateCentered(vec, angle, Axis.Y);
		});
	}
}
