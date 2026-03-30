package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;

// allows block modification to be done in transactions easily.
// this only modifies set/getBlockState.
public interface LevelExtensions {
	default SnapshotParticipant<LevelSnapshotData> port_lib_ufo$snapshotParticipant() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib_ufo$updateSnapshots(TransactionContext ctx) {
		port_lib_ufo$snapshotParticipant().updateSnapshots(ctx);
	}

	record LevelSnapshotData(List<ChangedPosData> changedStates) {
		public LevelSnapshotData(List<ChangedPosData> changedStates) {
			this.changedStates = changedStates == null ? null : new LinkedList<>(changedStates);
		}
	}

	record ChangedPosData(BlockPos pos, BlockState state, int flags) {
	}

	default void port_lib_ufo$addFreshBlockEntities(Collection<BlockEntity> beList) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib_ufo$markAndNotifyBlock(BlockPos pos, @Nullable LevelChunk levelchunk, BlockState oldState, BlockState newState, int flags, int p_46608_) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
