/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 *
 * Confirmed compatible with SecurityCraft
 */
@Mixin(value = ChunkMap.class, priority = 1200)
public abstract class ChunkMapMixin {
	@Shadow
	public abstract void updatePlayerStatus(ServerPlayer player, boolean added);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking methods
	 */
	@Redirect(method = {
			"getPlayers",
			"lambda$setViewDistance$0", "m_ntjylyau", "method_17219" // these 3 all refer to the same thing with different mappings
	}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"))
	private SectionPos securitycraft$getCameraSectionPos(ServerPlayer player) {
		if (ConductorPossessionController.isPossessingConductor(player) || player.getCamera().getClass().getName().equals("net.geforcemods.securitycraft.entity.camera.SecurityCamera"))
			return SectionPos.of(player.getCamera());

		return player.getLastSectionPos();
	}

	/**
	 * Tracks chunks loaded by cameras to send them to the client, and tracks chunks around the player to properly update them
	 * when they stop viewing a camera
	 *
	 * Note: In MC 1.21.1, ChunkMap.isChunkInRange() and the old updateChunkTracking() overload no longer exist.
	 * This method now uses updatePlayerStatus() to trigger a full chunk tracking refresh for the player when
	 * the conductor camera has not yet sent chunks.
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
		if (player.getCamera() instanceof ConductorEntity camera) {
			if (!camera.hasSentChunks()) {
				SectionPos pos = SectionPos.of(camera);
				camera.oldSectionPos = pos;

				// Trigger a full player status update so the server re-evaluates which chunks to send
				// based on the conductor camera's position (see securitycraft$getCameraSectionPos redirect above)
				this.updatePlayerStatus(player, false);
				this.updatePlayerStatus(player, true);

				camera.setHasSentChunks(true);
			}
		}
	}
}
