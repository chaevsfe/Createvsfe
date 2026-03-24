package com.simibubi.create.content.logistics.factoryBoard;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import com.simibubi.create.foundation.utility.Lang;

import io.netty.buffer.ByteBuf;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FactoryPanelBlock extends FaceAttachedHorizontalDirectionalBlock
	implements ProperWaterloggedBlock, IBE<FactoryPanelBlockEntity>, IWrenchable {
	public static final MapCodec<FactoryPanelBlock> CODEC = simpleCodec(FactoryPanelBlock::new);

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public enum PanelSlot implements StringRepresentable {
		TOP_LEFT(1, 1),
		TOP_RIGHT(0, 1),
		BOTTOM_LEFT(1, 0),
		BOTTOM_RIGHT(0, 0);

		public static final Codec<PanelSlot> CODEC = StringRepresentable.fromValues(PanelSlot::values);
		public static final StreamCodec<ByteBuf, PanelSlot> STREAM_CODEC = CreateStreamCodecs.ofEnum(PanelSlot.class);

		public final int xOffset;
		public final int yOffset;

		PanelSlot(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		@Override
		public @NotNull String getSerializedName() {
			return Lang.asId(name());
		}
	}

	public enum PanelState {
		PASSIVE, ACTIVE
	}

	public enum PanelType {
		NETWORK, PACKAGER
	}

	public FactoryPanelBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)
			.setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACE, FACING, WATERLOGGED, POWERED));
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		Direction connectedDir = getConnectedDirection(pState).getOpposite();
		BlockPos blockpos = pPos.relative(connectedDir);
		return !pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos).isEmpty();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState stateForPlacement = super.getStateForPlacement(pContext);
		if (stateForPlacement == null)
			return null;
		if (stateForPlacement.getValue(FACE) == AttachFace.FLOOR)
			stateForPlacement = stateForPlacement.setValue(FACING, stateForPlacement.getValue(FACING).getOpposite());
		return withWater(stateForPlacement.setValue(POWERED, false), pContext);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
		LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		updateWater(pLevel, pState, pPos);
		return pState;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return AllShapes.STOCK_LINK.get(getConnectedDirection(pState));
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
	public Class<FactoryPanelBlockEntity> getBlockEntityClass() {
		return FactoryPanelBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FactoryPanelBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.FACTORY_PANEL.get();
	}

	@Override
	protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	public static float getXRot(BlockState state) {
		AttachFace face = state.getValue(FACE);
		return face == AttachFace.CEILING ? Mth.HALF_PI : face == AttachFace.FLOOR ? -Mth.HALF_PI : 0;
	}

	public static float getYRot(BlockState state) {
		return state.getValue(FACING).toYRot() * Mth.DEG_TO_RAD;
	}

	public static Direction connectedDirection(BlockState state) {
		return getConnectedDirection(state);
	}

	public static PanelSlot getTargetedSlot(BlockPos pos, BlockState blockState, Vec3 clickLocation) {
		double bestDistance = Double.MAX_VALUE;
		PanelSlot bestSlot = PanelSlot.BOTTOM_LEFT;
		Vec3 localClick = clickLocation.subtract(Vec3.atLowerCornerOf(pos));
		float xRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(blockState);
		float yRot = Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(blockState);

		for (PanelSlot slot : PanelSlot.values()) {
			Vec3 vec = new Vec3(.25 + slot.xOffset * .5, 0, .25 + slot.yOffset * .5);
			vec = VecHelper.rotateCentered(vec, 180, Axis.Y);
			vec = VecHelper.rotateCentered(vec, xRot + 90, Axis.X);
			vec = VecHelper.rotateCentered(vec, yRot, Axis.Y);

			double diff = vec.distanceToSqr(localClick);
			if (diff > bestDistance)
				continue;
			bestDistance = diff;
			bestSlot = slot;
		}

		return bestSlot;
	}
}
