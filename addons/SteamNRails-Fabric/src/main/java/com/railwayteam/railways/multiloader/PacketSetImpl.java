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

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Function;

public class PacketSetImpl extends PacketSet {
    private CustomPacketPayload.Type<CRPayloadC2S> c2sPayloadType;
    private CustomPacketPayload.Type<CRPayloadS2C> s2cPayloadType;

    protected PacketSetImpl(String id, int version,
                            List<Function<FriendlyByteBuf, S2CPacket>> s2cPackets,
                            Object2IntMap<Class<? extends S2CPacket>> s2cTypes,
                            List<Function<FriendlyByteBuf, C2SPacket>> c2sPackets,
                            Object2IntMap<Class<? extends C2SPacket>> c2sTypes) {
        super(id, version, s2cPackets, s2cTypes, c2sPackets, c2sTypes);
        c2sPayloadType = new CustomPacketPayload.Type<>(c2sPacket);
        s2cPayloadType = new CustomPacketPayload.Type<>(s2cPacket);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerS2CListener() {
        // Register the S2C payload type and receiver
        PayloadTypeRegistry.playS2C().register(s2cPayloadType,
                StreamCodec.composite(ByteBufCodecs.BYTE_ARRAY, CRPayloadS2C::data, this::createS2CPayload));

        ClientPlayNetworking.registerGlobalReceiver(s2cPayloadType, (payload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
            handleS2CPacket(context.client(), buf);
        });
    }

    @Override
    public void registerC2SListener() {
        // Register the C2S payload type and receiver
        PayloadTypeRegistry.playC2S().register(c2sPayloadType,
                StreamCodec.composite(ByteBufCodecs.BYTE_ARRAY, CRPayloadC2S::data, this::createC2SPayload));

        ServerPlayNetworking.registerGlobalReceiver(c2sPayloadType, (payload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
            handleC2SPacket(context.player(), buf);
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void doSendC2S(FriendlyByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        ClientPlayNetworking.send(new CRPayloadC2S(c2sPayloadType, data));
    }

    /**
     * Send S2C data to a collection of players. Called from PlayerSelection.
     */
    void doSendS2C(Iterable<ServerPlayer> players, FriendlyByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        CRPayloadS2C payload = new CRPayloadS2C(s2cPayloadType, data);
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void send(SimplePacketBase packet) {
        AllPackets.getChannel().sendToServer(packet);
    }

    @Override
    public void sendTo(ServerPlayer player, SimplePacketBase packet) {
        AllPackets.getChannel().sendToClient(packet, player);
    }

    @Override
    public void sendTo(PlayerSelection selection, SimplePacketBase packet) {
        AllPackets.getChannel().sendToClients(packet, ((PlayerSelection.PlayerSelectionImpl) selection).players);
    }

    private CRPayloadC2S createC2SPayload(byte[] data) {
        return new CRPayloadC2S(c2sPayloadType, data);
    }

    private CRPayloadS2C createS2CPayload(byte[] data) {
        return new CRPayloadS2C(s2cPayloadType, data);
    }

    /**
     * CustomPayload for Client-to-Server packets.
     */
    record CRPayloadC2S(CustomPacketPayload.Type<CRPayloadC2S> payloadType, byte[] data) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return payloadType;
        }
    }

    /**
     * CustomPayload for Server-to-Client packets.
     */
    record CRPayloadS2C(CustomPacketPayload.Type<CRPayloadS2C> payloadType, byte[] data) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return payloadType;
        }
    }
}
