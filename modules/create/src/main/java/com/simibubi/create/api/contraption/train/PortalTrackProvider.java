package com.simibubi.create.api.contraption.train;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.trains.track.AllPortalTracks;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A provider for portal track connections.
 * Takes a track inbound through a portal and finds the exit location for the outbound track.
 * <p>
 * Use {@link AllPortalTracks#registerIntegration(Block, AllPortalTracks.PortalTrackProvider)} to register.
 */
@FunctionalInterface
public interface PortalTrackProvider {
	/**
	 * Find the exit location for a track going through a portal.
	 *
	 * @param level the level of the inbound track
	 * @param face  the face of the inbound track
	 * @return exit pair (level, face), or null if no exit found
	 */
	@Nullable
	Pair<ServerLevel, BlockFace> findExit(ServerLevel level, BlockFace face);

	/**
	 * Checks if a given {@link BlockState} represents a supported portal block.
	 *
	 * @param state The block state to check.
	 * @return {@code true} if the block state represents a supported portal; {@code false} otherwise.
	 */
	static boolean isSupportedPortal(BlockState state) {
		return AllPortalTracks.isSupportedPortal(state);
	}

	/**
	 * Retrieves the corresponding outbound track on the other side of a portal.
	 *
	 * @param level        The current {@link ServerLevel}.
	 * @param inboundTrack The inbound track {@link BlockFace}.
	 * @return the found outbound track, or null if one wasn't found.
	 */
	@Nullable
	static Pair<ServerLevel, BlockFace> getOtherSide(ServerLevel level, BlockFace inboundTrack) {
		return AllPortalTracks.getOtherSide(level, inboundTrack);
	}

	/**
	 * Register a portal track provider for a specific block type.
	 */
	static void register(ResourceLocation blockId, PortalTrackProvider provider) {
		AllPortalTracks.registerIntegration(blockId, inbound -> provider.findExit(inbound.getFirst(), inbound.getSecond()));
	}

	/**
	 * Register a portal track provider for a specific block.
	 */
	static void register(Block block, PortalTrackProvider provider) {
		AllPortalTracks.registerIntegration(block, inbound -> provider.findExit(inbound.getFirst(), inbound.getSecond()));
	}

	record Exit(ServerLevel level, BlockFace face) {
	}
}
