package com.simibubi.create.content.logistics.packagePort.frogport;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FrogportBlock extends Block implements IBE<FrogportBlockEntity>, IWrenchable {

	public static final MapCodec<FrogportBlock> CODEC = simpleCodec(FrogportBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	public FrogportBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return AllShapes.PACKAGE_PORT;
	}

	// Frogport uses NORMAL push reaction (default for blocks)

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
		if (pPlacer == null)
			return;
		withBlockEntityDo(pLevel, pPos, be -> {
			Vec3 diff = Vec3.atCenterOf(pPos).subtract(pPlacer.position());
			be.passiveYaw = (float) (Mth.atan2(diff.x, diff.z) * Mth.RAD_TO_DEG);
			be.passiveYaw = Math.round(be.passiveYaw / 11.25f) * 11.25f;
			be.notifyUpdate();
		});
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
		Player player, InteractionHand hand, BlockHitResult hitResult) {
		return onBlockEntityUse(level, pos, be -> be.use(player));
	}

	@Override
	public Class<FrogportBlockEntity> getBlockEntityClass() {
		return FrogportBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FrogportBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.PACKAGE_FROGPORT.get();
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return getBlockEntityOptional(pLevel, pPos).map(pbe -> pbe.getComparatorOutput())
			.orElse(0);
	}
}
