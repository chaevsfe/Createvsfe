package com.simibubi.create.content.logistics.tableCloth;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TableClothBlock extends Block implements IHaveBigOutline, IWrenchable, IBE<TableClothBlockEntity> {

	public static final BooleanProperty HAS_BE = BooleanProperty.create("entity");

	private DyeColor colour;

	public TableClothBlock(Properties pProperties, DyeColor colour) {
		super(pProperties);
		this.colour = colour;
		registerDefaultState(defaultBlockState().setValue(HAS_BE, false));
	}

	public TableClothBlock(Properties pProperties, String type) {
		super(pProperties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(HAS_BE));
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (!(pPlacer instanceof Player player))
			return;

		AutoRequestData requestData = AutoRequestData.readFromItem(pLevel, player, pPos, pStack);
		if (requestData == null)
			return;

		pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_BE, true));
		withBlockEntityDo(pLevel, pPos, dcbe -> {
			dcbe.requestData = requestData;
			dcbe.owner = player.getUUID();
			dcbe.facing = player.getDirection().getOpposite();
		});
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
		Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (hitResult.getDirection() == Direction.DOWN)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level.isClientSide)
			return ItemInteractionResult.SUCCESS;

		boolean shiftKeyDown = player.isShiftKeyDown();
		if (!player.mayBuild())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if ((shiftKeyDown || stack.isEmpty()) && !state.getValue(HAS_BE))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (!level.isClientSide() && !state.getValue(HAS_BE))
			level.setBlockAndUpdate(pos, state.cycle(HAS_BE));

		return onBlockEntityUse(level, pos, dcbe -> dcbe.use(player, hitResult));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return AllShapes.TABLE_CLOTH;
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return AllShapes.TABLE_CLOTH_OCCLUSION;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
		CollisionContext pContext) {
		return AllShapes.TABLE_CLOTH_OCCLUSION;
	}

	@Override
	public boolean canSurvive(BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
		return true;
	}

	@Nullable
	public DyeColor getColor() {
		return colour;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HAS_BE) ? IBE.super.newBlockEntity(pos, state) : null;
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		if (!pNewState.getOptionalValue(HAS_BE).orElse(false))
			pNewState = Blocks.AIR.defaultBlockState();
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	@Override
	public Class<TableClothBlockEntity> getBlockEntityClass() {
		return TableClothBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TableClothBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.TABLE_CLOTH.get();
	}
}
