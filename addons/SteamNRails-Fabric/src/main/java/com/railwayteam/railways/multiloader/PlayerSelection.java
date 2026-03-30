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

package com.railwayteam.railways.multiloader;

import io.github.fabricators_of_create.porting_lib_ufo.util.ServerLifecycleHooks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;

/**
 * Find players to send S2C packets to.
 */
public abstract class PlayerSelection {
	public abstract void accept(ResourceLocation id, FriendlyByteBuf buffer);

	public static PlayerSelection all()  {
        return new PlayerSelectionImpl(PlayerLookup.all(ServerLifecycleHooks.getCurrentServer()));
    }

	public static PlayerSelection allWith(Predicate<ServerPlayer> condition)  {
        return new PlayerSelectionImpl(PlayerLookup.all(ServerLifecycleHooks.getCurrentServer()).stream()
			.filter(condition).toList());
    }

	public static PlayerSelection of(ServerPlayer player)  {
        return new PlayerSelectionImpl(Collections.singleton(player));
    }

	public static PlayerSelection tracking(Entity entity)  {
        return new PlayerSelectionImpl(PlayerLookup.tracking(entity));
    }

	public static PlayerSelection trackingWith(Entity entity, Predicate<ServerPlayer> condition)  {
        return new PlayerSelectionImpl(PlayerLookup.tracking(entity).stream().filter(condition).toList());
    }

	public static PlayerSelection tracking(BlockEntity be)  {
        return new PlayerSelectionImpl(PlayerLookup.tracking(be));
    }

	public static PlayerSelection tracking(ServerLevel level, BlockPos pos)  {
        return new PlayerSelectionImpl(PlayerLookup.tracking(level, pos));
    }

	public static PlayerSelection trackingAndSelf(ServerPlayer player)  {
        ArrayList<ServerPlayer> players = new ArrayList<>(PlayerLookup.tracking(player));
		players.add(player);
		return new PlayerSelectionImpl(players);
    }

	static class PlayerSelectionImpl extends PlayerSelection {
		final Collection<ServerPlayer> players;

		PlayerSelectionImpl(Collection<ServerPlayer> players) {
			this.players = players;
		}

		@Override
		public void accept(ResourceLocation id, FriendlyByteBuf buffer) {
			// The id is the S2C channel ResourceLocation from a PacketSet.
			// Look up the PacketSet that owns this channel and delegate to its doSendS2C.
			// Since CRPackets.PACKETS is the only PacketSet, we access it via the registry pattern.
			PacketSet packetSet = PacketSet.getByS2CChannel(id);
			if (packetSet instanceof PacketSetImpl impl) {
				impl.doSendS2C(players, buffer);
			}
		}
	}
}
